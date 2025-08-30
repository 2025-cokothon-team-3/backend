package com.example.cokothon.service;

import com.example.cokothon.entity.ComparisonResult;
import com.example.cokothon.repository.ComparisonResultRepository;
import com.example.cokothon.dto.TestResultDto;
import com.example.cokothon.dto.ComparisonAnalysisDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class TravelComparisonService {

    private final GeminiService geminiService;
    private final TestResultService testResultService;
    private final ComparisonResultRepository comparisonResultRepository;

    /**
     * 그룹 여행 호환성 분석 (캐시 우선, 없으면 새로 생성)
     */
    @Transactional
    @Cacheable(value = "travel-analysis", key = "#userIds", unless = "#result == null")
    public ComparisonAnalysisDto analyzeGroupCompatibility(List<Long> userIds) {
        // 사용자 ID 정렬하여 일관된 키 생성
        List<Long> sortedIds = userIds.stream().sorted().collect(Collectors.toList());
        String userIdsKey = sortedIds.stream().map(String::valueOf).collect(Collectors.joining(","));

        // 기존 분석 결과 확인 (최근 7일 내)
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);
        Optional<ComparisonResult> existingResult = comparisonResultRepository
                .findRecentByUserIds(userIdsKey, cutoffDate);

        if (existingResult.isPresent()) {
            log.info("기존 분석 결과 사용: {}", userIdsKey);
            return convertToDto(existingResult.get(), sortedIds);
        }

        // 새로운 분석 생성
        return generateNewAnalysis(sortedIds, userIdsKey);
    }

    /**
     * 새로운 분석 결과 생성 및 저장
     */
    private ComparisonAnalysisDto generateNewAnalysis(List<Long> userIds, String userIdsKey) {
        try {
            List<TestResultDto> results = testResultService.getUsersTestResults(userIds);

            // 호환성 점수 계산
            int compatibilityScore = calculateCompatibilityScore(results);

            // Gemini AI 분석 생성
            String prompt = createOptimizedPrompt(results, compatibilityScore);
            String aiResponse = geminiService.generateTextSync(prompt);

            // AI 응답 파싱
            Map<String, String> parsedResponse = parseAiResponse(aiResponse);

            // 분석 결과 저장
            ComparisonResult comparisonResult = ComparisonResult.builder()
                    .userIds(userIdsKey)
                    .aiFeedback(parsedResponse.get("analysis"))
                    .compatibilityScore(compatibilityScore)
                    .recommendations(parsedResponse.get("recommendations"))
                    .warningPoints(parsedResponse.get("warnings"))
                    .build();

            comparisonResultRepository.save(comparisonResult);

            log.info("새로운 분석 결과 생성 완료: {} (점수: {})", userIdsKey, compatibilityScore);
            return convertToDto(comparisonResult, userIds);

        } catch (Exception e) {
            log.error("분석 생성 중 오류 발생: ", e);
            // 기본 분석 제공
            return createFallbackAnalysis(userIds, userIdsKey);
        }
    }

    /**
     * 최적화된 Gemini 프롬프트 생성
     */
    private String createOptimizedPrompt(List<TestResultDto> results, int compatibilityScore) {
        String groupSummary = generateGroupSummary(results);
        String conflictPoints = identifyPotentialConflicts(results);

        return String.format("""
            여행 컨설턴트로서 %d명 그룹의 호환성을 분석해주세요.
            
            그룹 구성: %s
            호환성 점수: %d/100점
            예상 갈등: %s
            
            다음 형식으로 정확히 구분해서 답변하세요:
            
            [ANALYSIS]
            그룹의 전체적인 여행 성향과 특징을 2-3문장으로 설명
            
            [RECOMMENDATIONS]  
            이 그룹에게 가장 적합한 여행 스타일과 추천 활동을 2-3문장으로 제안
            
            [WARNINGS]
            여행 중 주의해야 할 갈등 요소와 해결 방법을 2문장으로 조언
            
            각 섹션은 간결하고 실용적으로 작성해주세요.
            """,
                results.size(), groupSummary, compatibilityScore, conflictPoints
        );
    }

    /**
     * 그룹 요약 생성
     */
    private String generateGroupSummary(List<TestResultDto> results) {
        Map<String, Long> typeCount = results.stream()
                .collect(Collectors.groupingBy(TestResultDto::getDominantType, Collectors.counting()));

        return typeCount.entrySet().stream()
                .map(entry -> entry.getKey() + "(" + entry.getValue() + "명)")
                .collect(Collectors.joining(", "));
    }

    /**
     * 잠재적 갈등 요소 식별
     */
    private String identifyPotentialConflicts(List<TestResultDto> results) {
        StringBuilder conflicts = new StringBuilder();

        boolean planningConflict = hasConflict(results, TestResultDto::getPlanningType);
        boolean budgetConflict = hasConflict(results, TestResultDto::getBudgetType);
        boolean activityConflict = hasConflict(results, TestResultDto::getActivityType);

        if (planningConflict) conflicts.append("계획방식 차이, ");
        if (budgetConflict) conflicts.append("예산수준 차이, ");
        if (activityConflict) conflicts.append("활동성향 차이, ");

        return conflicts.length() > 0 ?
                conflicts.toString().replaceAll(", $", "") : "주요 갈등 요소 없음";
    }

    /**
     * 특정 성향에서 갈등 여부 확인
     */
    private boolean hasConflict(List<TestResultDto> results,
                                java.util.function.Function<TestResultDto, String> extractor) {
        return results.stream().map(extractor).distinct().count() > 1;
    }

    /**
     * 호환성 점수 계산 (간소화된 버전)
     */
    private int calculateCompatibilityScore(List<TestResultDto> results) {
        if (results.size() < 2) return 100;

        int totalScore = 0;
        int pairCount = 0;

        for (int i = 0; i < results.size(); i++) {
            for (int j = i + 1; j < results.size(); j++) {
                totalScore += calculatePairScore(results.get(i), results.get(j));
                pairCount++;
            }
        }

        return pairCount > 0 ? totalScore / pairCount : 85;
    }

    /**
     * 두 사용자 간 호환성 점수
     */
    private int calculatePairScore(TestResultDto user1, TestResultDto user2) {
        int score = 70; // 기본 점수

        // 각 성향별 점수 조정
        score += calculateCategoryScore(user1.getPlanningType(), user2.getPlanningType(), 8);
        score += calculateCategoryScore(user1.getBudgetType(), user2.getBudgetType(), 10);
        score += calculateCategoryScore(user1.getActivityType(), user2.getActivityType(), 7);
        score += calculateCategoryScore(user1.getSocialType(), user2.getSocialType(), 5);

        return Math.max(30, Math.min(100, score));
    }

    private int calculateCategoryScore(String type1, String type2, int maxPoints) {
        return type1.equals(type2) ? maxPoints : -(maxPoints / 2);
    }

    /**
     * AI 응답 파싱
     */
    private Map<String, String> parseAiResponse(String response) {
        Map<String, String> result = new HashMap<>();

        try {
            String[] sections = response.split("\\[");

            for (String section : sections) {
                if (section.startsWith("ANALYSIS]")) {
                    result.put("analysis", extractContent(section, "ANALYSIS]"));
                } else if (section.startsWith("RECOMMENDATIONS]")) {
                    result.put("recommendations", extractContent(section, "RECOMMENDATIONS]"));
                } else if (section.startsWith("WARNINGS]")) {
                    result.put("warnings", extractContent(section, "WARNINGS]"));
                }
            }

            // 기본값 설정
            result.putIfAbsent("analysis", response.substring(0, Math.min(200, response.length())));
            result.putIfAbsent("recommendations", "개별 성향을 고려한 균형 잡힌 여행을 추천합니다.");
            result.putIfAbsent("warnings", "사전 충분한 소통을 통해 갈등을 예방하세요.");

        } catch (Exception e) {
            log.warn("AI 응답 파싱 실패, 기본값 사용: {}", e.getMessage());
            result.put("analysis", response);
            result.put("recommendations", "그룹 성향을 고려한 맞춤 여행을 추천합니다.");
            result.put("warnings", "여행 전 충분한 계획과 소통이 필요합니다.");
        }

        return result;
    }

    private String extractContent(String section, String header) {
        return section.replace(header, "").trim().split("\\n\\[")[0].trim();
    }

    /**
     * ComparisonResult -> ComparisonAnalysisDto 변환
     */
    private ComparisonAnalysisDto convertToDto(ComparisonResult result, List<Long> userIds) {
        return ComparisonAnalysisDto.builder()
                .userIds(userIds)
                .memberCount(userIds.size())
                .compatibilityScore(result.getCompatibilityScore())
                .analysis(result.getAiFeedback())
                .recommendations(result.getRecommendations())
                .warningPoints(result.getWarningPoints())
                .analysisDate(result.getCreatedAt())
                .build();
    }

    /**
     * 오류 시 대체 분석 제공
     */
    private ComparisonAnalysisDto createFallbackAnalysis(List<Long> userIds, String userIdsKey) {
        int baseScore = 75 + (int)(Math.random() * 20); // 75-95 랜덤

        return ComparisonAnalysisDto.builder()
                .userIds(userIds)
                .memberCount(userIds.size())
                .compatibilityScore(baseScore)
                .analysis(String.format("%d명으로 구성된 여행 그룹입니다. 다양한 성향이 조화를 이루어 흥미로운 여행이 될 것으로 예상됩니다.", userIds.size()))
                .recommendations("각자의 선호도를 고려한 일정을 짜고, 개인 시간과 단체 시간을 적절히 배분하는 것을 추천합니다.")
                .warningPoints("여행 전 예산과 일정에 대해 충분히 논의하여 갈등을 미리 예방하시기 바랍니다.")
                .build();
    }

    /**
     * 사용자별 분석 이력 조회
     */
    @Transactional(readOnly = true)
    public List<ComparisonAnalysisDto> getUserAnalysisHistory(Long userId) {
        List<ComparisonResult> results = comparisonResultRepository
                .findByUserIdsContaining(userId.toString());

        return results.stream()
                .map(result -> {
                    List<Long> ids = parseUserIds(result.getUserIds());
                    return convertToDto(result, ids);
                })
                .collect(Collectors.toList());
    }

    private List<Long> parseUserIds(String userIds) {
        return List.of(userIds.split(",")).stream()
                .map(String::trim)
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "compatibility-scores", key = "#userIds")
    public int calculateQuickCompatibilityScore(List<Long> userIds) {
        List<TestResultDto> results = testResultService.getUsersTestResults(userIds);
        return calculateCompatibilityScore(results);
    }

    /**
     * 인기 조합 분석
     */
    public List<String> getPopularCombinations() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        List<ComparisonResult> recentResults = comparisonResultRepository.findRecentResults(cutoffDate);

        return recentResults.stream()
                .collect(Collectors.groupingBy(ComparisonResult::getUserIds, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(entry -> {
                    String[] ids = entry.getKey().split(",");
                    return String.format("%d명 그룹 (%d회 분석)", ids.length, entry.getValue());
                })
                .collect(Collectors.toList());
    }
}