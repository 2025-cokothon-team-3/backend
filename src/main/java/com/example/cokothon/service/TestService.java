package com.example.cokothon.service;

import com.example.cokothon.entity.*;
import com.example.cokothon.repository.*;
import com.example.cokothon.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final TestResultRepository testResultRepository;
    private final TravelPersonalityRepository personalityRepository;

    /**
     * 테스트 제출 및 결과 계산
     */
    @Transactional
    public Map<String, Object> submitTest(Long userId, SubmitTestRequestDto requestDto) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

            List<Question> questions = questionRepository.findAllByOrderByQuestionOrder();
            if (questions.size() != 16) {
                throw new IllegalStateException("질문 데이터가 올바르지 않습니다. 16개 질문이 필요합니다.");
            }

            validateAnswers(requestDto.getAnswers(), questions);

            deleteExistingAnswers(userId);

            List<UserAnswer> savedAnswers = saveUserAnswers(userId, requestDto.getAnswers());

            TestResult testResult = calculateAndSaveTestResult(userId, savedAnswers, questions);

            TestResultDto resultDto = convertToTestResultDto(testResult);

            Map<String, Object> personalityDetails = getPersonalityDetails(resultDto);

            log.info("사용자 {}의 테스트가 성공적으로 완료되었습니다. 결과: {}",
                    userId, testResult.getDominantType());

            return Map.of(
                    "testResult", resultDto,
                    "personalityDetails", personalityDetails
            );

        } catch (Exception e) {
            log.error("테스트 제출 중 오류 발생: ", e);
            throw e;
        }
    }

    private Map<String, Object> getPersonalityDetails(TestResultDto result) {
        List<String> names = List.of(
                result.getPlanningType(), result.getBudgetType(),
                result.getActivityType(), result.getSocialType()
        );

        List<TravelPersonality> personalities =
                personalityRepository.findByNameInAndIsDeletedFalse(names);

        Map<String, Object> details = new HashMap<>();
        for (TravelPersonality p : personalities) {
            String key = p.getCategory().name().toLowerCase();
            details.put(key, Map.of(
                    "name", p.getName(),
                    "description", p.getDescription(),
                    "iconUrl", p.getIconUrl(),
                    "colorCode", p.getColorCode()
            ));
        }
        return details;
    }

    /**
     * 답변 유효성 검증
     */
    private void validateAnswers(List<SubmitTestRequestDto.UserAnswerDto> answers, List<Question> questions) {
        if (answers == null || answers.size() != 16) {
            throw new IllegalArgumentException("16개의 답변이 모두 필요합니다.");
        }

        List<Long> questionIds = questions.stream()
                .map(Question::getId)
                .collect(Collectors.toList());

        List<Long> answeredQuestionIds = answers.stream()
                .map(SubmitTestRequestDto.UserAnswerDto::getQuestionId)
                .collect(Collectors.toList());

        for (Long questionId : questionIds) {
            if (!answeredQuestionIds.contains(questionId)) {
                throw new IllegalArgumentException("질문 " + questionId + "에 대한 답변이 없습니다.");
            }
        }

        long distinctCount = answeredQuestionIds.stream().distinct().count();
        if (distinctCount != answers.size()) {
            throw new IllegalArgumentException("중복된 질문에 대한 답변이 있습니다.");
        }

        for (SubmitTestRequestDto.UserAnswerDto answer : answers) {
            if (answer.getSelectedChoice() < 1 || answer.getSelectedChoice() > 3) {
                throw new IllegalArgumentException("선택 답변은 1-3 사이여야 합니다.");
            }
        }
    }

    /**
     * 기존 답변 및 결과 삭제 (재테스트 허용)
     */
    private void deleteExistingAnswers(Long userId) {
        List<UserAnswer> existingAnswers = userAnswerRepository.findByUserIdOrderByQuestionId(userId);
        if (!existingAnswers.isEmpty()) {
            userAnswerRepository.deleteAll(existingAnswers);
        }

        testResultRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .ifPresent(testResultRepository::delete);
    }

    /**
     * 사용자 답변 저장
     */
    private List<UserAnswer> saveUserAnswers(Long userId, List<SubmitTestRequestDto.UserAnswerDto> answers) {
        List<UserAnswer> userAnswers = answers.stream()
                .map(answerDto -> UserAnswer.builder()
                        .userId(userId)
                        .questionId(answerDto.getQuestionId())
                        .selectedChoice(answerDto.getSelectedChoice())
                        .build())
                .collect(Collectors.toList());

        return userAnswerRepository.saveAll(userAnswers);
    }

    /**
     * 테스트 결과 계산 및 저장
     */
    private TestResult calculateAndSaveTestResult(Long userId, List<UserAnswer> answers, List<Question> questions) {
        Map<Long, QuestionCategory> questionCategoryMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, Question::getCategory));

        // 카테고리별 점수 계산
        int planningScore = calculateCategoryScore(answers, questionCategoryMap, QuestionCategory.PLANNING);
        int budgetScore = calculateCategoryScore(answers, questionCategoryMap, QuestionCategory.BUDGET);
        int activityScore = calculateCategoryScore(answers, questionCategoryMap, QuestionCategory.ACTIVITY);
        int socialScore = calculateCategoryScore(answers, questionCategoryMap, QuestionCategory.SOCIAL);

        // 성향 타입 결정 (6점 기준)
        String planningType = planningScore >= 6 ? "계획형" : "즉흥형";
        String budgetType = budgetScore >= 6 ? "럭셔리형" : "절약형";
        String activityType = activityScore >= 6 ? "액티브형" : "휴식형";
        String socialType = socialScore >= 6 ? "사교형" : "개인형";

        // 주요 성향 조합 생성
        String dominantType = generateDominantType(planningType, budgetType, activityType, socialType);

        // 테스트 결과 저장
        TestResult testResult = TestResult.builder()
                .userId(userId)
                .planningScore(planningScore)
                .budgetScore(budgetScore)
                .activityScore(activityScore)
                .socialScore(socialScore)
                .planningType(planningType)
                .budgetType(budgetType)
                .activityType(activityType)
                .socialType(socialType)
                .dominantType(dominantType)
                .build();

        return testResultRepository.save(testResult);
    }

    /**
     * 카테고리별 점수 계산
     */
    private int calculateCategoryScore(List<UserAnswer> answers,
                                       Map<Long, QuestionCategory> questionCategoryMap,
                                       QuestionCategory category) {
        return answers.stream()
                .filter(answer -> category.equals(questionCategoryMap.get(answer.getQuestionId())))
                .mapToInt(UserAnswer::getSelectedChoice)
                .sum();
    }

    /**
     * 주요 성향 조합 생성
     */
    private String generateDominantType(String planning, String budget, String activity, String social) {
        String planningShort = planning.equals("계획형") ? "계획형" : "즉흥형";
        String budgetShort = budget.equals("럭셔리형") ? "럭셔리" : "절약";

        if (activity.equals("액티브형") && social.equals("사교형")) {
            return planningShort + " 액티브 사교형";
        } else if (activity.equals("휴식형") && social.equals("개인형")) {
            return planningShort + " 힐링 개인형";
        } else {
            return planningShort + " " + budgetShort + "러";
        }
    }

    /**
     * TestResult를 TestResultDto로 변환
     */
    private TestResultDto convertToTestResultDto(TestResult testResult) {
        return TestResultDto.builder()
                .planningType(testResult.getPlanningType())
                .budgetType(testResult.getBudgetType())
                .activityType(testResult.getActivityType())
                .socialType(testResult.getSocialType())
                .planningScore(testResult.getPlanningScore())
                .budgetScore(testResult.getBudgetScore())
                .activityScore(testResult.getActivityScore())
                .socialScore(testResult.getSocialScore())
                .dominantType(testResult.getDominantType())
                .build();
    }

    /**
     * 사용자별 테스트 결과 조회
     */
    @Transactional(readOnly = true)
    public TestResultDto getUserTestResult(Long userId) {
        TestResult testResult = testResultRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new IllegalArgumentException("테스트 결과를 찾을 수 없습니다: " + userId));

        return convertToTestResultDto(testResult);
    }

    /**
     * 사용자가 테스트를 완료했는지 확인
     */
    @Transactional(readOnly = true)
    public boolean hasUserCompletedTest(Long userId) {
        return testResultRepository.findTopByUserIdOrderByCreatedAtDesc(userId).isPresent();
    }
}