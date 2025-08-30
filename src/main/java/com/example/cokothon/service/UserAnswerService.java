package com.example.cokothon.service;

import com.example.cokothon.entity.UserAnswer;
import com.example.cokothon.entity.Question;
import com.example.cokothon.repository.UserAnswerRepository;
import com.example.cokothon.repository.QuestionRepository;
import com.example.cokothon.dto.UserAnswerRequestDto;
import com.example.cokothon.dto.UserAnswerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserAnswerService {

    private final UserAnswerRepository userAnswerRepository;
    private final QuestionRepository questionRepository;

    /**
     * 사용자 답변 저장
     */
    @Transactional
    public UserAnswerDto saveUserAnswer(UserAnswerRequestDto request) {
        // 질문 존재 여부 확인
        Question question = questionRepository.findByIdAndIsDeletedFalse(request.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 질문입니다: " + request.getQuestionId()));

        // 이미 답변한 질문인지 확인
        Optional<UserAnswer> existingAnswer = userAnswerRepository
                .findByUserIdAndQuestionIdAndIsDeletedFalse(request.getUserId(), request.getQuestionId());

        UserAnswer userAnswer;
        if (existingAnswer.isPresent()) {
            // 기존 답변 수정
            userAnswer = existingAnswer.get();
            userAnswer = UserAnswer.builder()
                    .id(userAnswer.getId())
                    .userId(request.getUserId())
                    .questionId(request.getQuestionId())
                    .selectedChoice(request.getSelectedChoice())
                    .isDeleted(false)
                    .createdAt(userAnswer.getCreatedAt())
                    .build();
            log.info("기존 답변 수정: userId={}, questionId={}", request.getUserId(), request.getQuestionId());
        } else {
            // 새 답변 저장
            userAnswer = UserAnswer.builder()
                    .userId(request.getUserId())
                    .questionId(request.getQuestionId())
                    .selectedChoice(request.getSelectedChoice())
                    .isDeleted(false)
                    .build();
            log.info("새 답변 저장: userId={}, questionId={}", request.getUserId(), request.getQuestionId());
        }

        userAnswer = userAnswerRepository.save(userAnswer);
        return convertToDto(userAnswer, question);
    }

    /**
     * 사용자의 모든 답변 조회
     */
    public List<UserAnswerDto> getUserAnswers(Long userId) {
        List<UserAnswer> answers = userAnswerRepository.findByUserIdAndIsDeletedFalseOrderByQuestionId(userId);

        return answers.stream()
                .map(answer -> {
                    Question question = questionRepository.findById(answer.getQuestionId())
                            .orElse(null);
                    return convertToDto(answer, question);
                })
                .collect(Collectors.toList());
    }

    /**
     * 답변 수정
     */
    @Transactional
    public UserAnswerDto updateUserAnswer(Long answerId, UserAnswerRequestDto request) {
        UserAnswer userAnswer = userAnswerRepository.findByIdAndIsDeletedFalse(answerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 답변입니다: " + answerId));

        Question question = questionRepository.findByIdAndIsDeletedFalse(request.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 질문입니다: " + request.getQuestionId()));

        // 답변 수정
        UserAnswer updatedAnswer = UserAnswer.builder()
                .id(userAnswer.getId())
                .userId(request.getUserId())
                .questionId(request.getQuestionId())
                .selectedChoice(request.getSelectedChoice())
                .isDeleted(false)
                .createdAt(userAnswer.getCreatedAt())
                .build();

        updatedAnswer = userAnswerRepository.save(updatedAnswer);
        return convertToDto(updatedAnswer, question);
    }

    /**
     * 사용자가 답변한 질문 수 조회
     */
    public Long getUserAnswerCount(Long userId) {
        return userAnswerRepository.countByUserIdAndIsDeletedFalse(userId);
    }

    /**
     * 사용자의 특정 질문 답변 조회
     */
    public Optional<UserAnswerDto> getUserAnswerByQuestion(Long userId, Long questionId) {
        Optional<UserAnswer> answer = userAnswerRepository
                .findByUserIdAndQuestionIdAndIsDeletedFalse(userId, questionId);

        if (answer.isPresent()) {
            Question question = questionRepository.findById(questionId).orElse(null);
            return Optional.of(convertToDto(answer.get(), question));
        }

        return Optional.empty();
    }

    /**
     * UserAnswer를 UserAnswerDto로 변환
     */
    private UserAnswerDto convertToDto(UserAnswer userAnswer, Question question) {
        String selectedChoiceText = null;
        if (question != null && userAnswer.getSelectedChoice() != null) {
            switch (userAnswer.getSelectedChoice()) {
                case 1 -> selectedChoiceText = question.getChoice1();
                case 2 -> selectedChoiceText = question.getChoice2();
                case 3 -> selectedChoiceText = question.getChoice3();
            }
        }

        return UserAnswerDto.builder()
                .id(userAnswer.getId())
                .userId(userAnswer.getUserId())
                .questionId(userAnswer.getQuestionId())
                .questionOrder(question != null ? question.getQuestionOrder() : null)
                .questionContent(question != null ? question.getContent() : null)
                .selectedChoice(userAnswer.getSelectedChoice())
                .selectedChoiceText(selectedChoiceText)
                .createdAt(userAnswer.getCreatedAt())
                .updatedAt(userAnswer.getUpdatedAt())
                .build();
    }
}