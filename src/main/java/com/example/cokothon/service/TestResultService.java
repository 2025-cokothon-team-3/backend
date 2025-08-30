package com.example.cokothon.service;

import com.example.cokothon.entity.TestResult;
import com.example.cokothon.repository.TestResultRepository;
import com.example.cokothon.dto.TestResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestResultService {

    private final TestResultRepository testResultRepository;

    /**
     * 사용자별 테스트 결과 조회
     */
    public TestResultDto getUserTestResult(Long userId) {
        TestResult testResult = testResultRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new IllegalArgumentException("테스트 결과를 찾을 수 없습니다: " + userId));

        return convertToDto(testResult);
    }

    /**
     * 여러 사용자의 테스트 결과 조회
     */
    public List<TestResultDto> getUsersTestResults(List<Long> userIds) {
        List<TestResult> testResults = testResultRepository.findByUserIdInOrderByCreatedAtDesc(userIds);
        return testResults.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 특정 성향 타입 사용자들 조회
     */
    public List<TestResultDto> getUsersByDominantType(String dominantType) {
        List<TestResult> testResults = testResultRepository.findByDominantTypeContaining(dominantType);
        return testResults.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 사용자가 테스트를 완료했는지 확인
     */
    public boolean hasUserCompletedTest(Long userId) {
        return testResultRepository.findTopByUserIdOrderByCreatedAtDesc(userId).isPresent();
    }

    /**
     * TestResult를 TestResultDto로 변환
     */
    private TestResultDto convertToDto(TestResult testResult) {
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
}