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
     * 사용자 친화적인 Gemini 프롬프트 생성
     */
    private String createOptimizedPrompt(List<TestResultDto> results, int compatibilityScore) {
        String groupSummary = generateFriendlyGroupSummary(results);
        String conflictPoints = identifyPotentialConflicts(results);

        return String.format("""
        친구들과 함께 여행가는 %d명 그룹을 분석해주세요. 마치 여행을 많이 다녀본 친한 친구가 조언해주는 것처럼 편하고 재미있게 말해주세요.
        
        그룹 구성: %s
        궁합 점수: %d점
        주의할 점: %s
        
        다음처럼 답변해주세요:
        
        [ANALYSIS]
        이 그룹의 여행 스타일을 친근하게 설명해주세요. "너희 그룹은..." 이런 식으로 시작해서 2-3문장으로.
        
        [RECOMMENDATIONS]
        구체적이고 실용적인 여행 추천을 해주세요. "이런 여행 어때?" 하는 느낌으로 2-3문장.
        
        [WARNINGS]
        여행 중 조심해야 할 점을 솔직하지만 부드럽게 알려주세요. 해결 방법도 같이. 1-2문장.
        
        전체적으로 따뜻하고 유머러스하게, 하지만 실용적인 조언이 되도록 작성해주세요.
        """,
                results.size(), groupSummary, compatibilityScore, conflictPoints
        );
    }

    /**
     * 친근한 그룹 요약 생성
     */
    private String generateFriendlyGroupSummary(List<TestResultDto> results) {
        Map<String, Long> typeCount = results.stream()
                .collect(Collectors.groupingBy(TestResultDto::getDominantType, Collectors.counting()));

        StringBuilder summary = new StringBuilder();

        typeCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> {
                    String friendlyType = makeFriendlyType(entry.getKey());
                    if (entry.getValue() == 1) {
                        summary.append(friendlyType).append(" 1명, ");
                    } else {
                        summary.append(friendlyType).append(" ").append(entry.getValue()).append("명, ");
                    }
                });

        return summary.toString().replaceAll(", $", "");
    }

    /**
     * 성향을 친근하게 변환
     */
    private String makeFriendlyType(String originalType) {
        // 딱딱한 용어를 친근하게 변환
        return originalType
                .replace("계획형", "미리미리 계획러")
                .replace("즉흥형", "그때그때 즉흥러")
                .replace("럭셔리", "좋은 거 좋아하는")
                .replace("절약", "가성비 추구하는")
                .replace("액티브", "활발한")
                .replace("휴식", "여유로운")
                .replace("사교", "사람 좋아하는")
                .replace("개인", "나만의 시간 소중한");
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
     * AI 응답 파싱 - 더 유연한 처리
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

            // 기본값을 더 친근하게 설정
            result.putIfAbsent("analysis",
                    "너희 그룹은 각자 다른 매력이 있어서 여행이 재미있을 것 같아! 서로 다른 점들이 오히려 여행을 더 풍성하게 만들어줄 거야.");
            result.putIfAbsent("recommendations",
                    "각자 좋아하는 걸 조금씩 섞은 여행 코스를 짜보는 건 어때? 계획도 적당히, 예산도 적당히 맞춰서 모두가 만족할 수 있을 거야.");
            result.putIfAbsent("warnings",
                    "가장 중요한 건 서로 배려하는 마음! 여행 전에 예산이나 일정 같은 중요한 부분은 미리 얘기해두면 더 즐거운 여행이 될 거야.");

        } catch (Exception e) {
            log.warn("AI 응답 파싱 실패, 기본값 사용: {}", e.getMessage());
            result.put("analysis", "너희 그룹만의 특별한 매력이 있을 거야! 함께 여행하면서 새로운 추억을 많이 만들어보자.");
            result.put("recommendations", "서로의 취향을 존중하면서 즐거운 여행 계획을 세워보길 추천해!");
            result.put("warnings", "여행 전에 중요한 것들은 미리 상의하고, 서로 이해하는 마음으로 떠나면 최고의 여행이 될 거야!");
        }

        return result;
    }

    /**
     * 대체 분석도 더 친근하게
     */
    private ComparisonAnalysisDto createFallbackAnalysis(List<Long> userIds, String userIdsKey) {
        int baseScore = 75 + (int)(Math.random() * 20); // 75-95 랜덤

        String[] friendlyAnalyses = {
                "너희 %d명이 함께하는 여행이면 분명 재미있을 거야! 각자 다른 매력이 있어서 여행이 더 다채로워질 것 같아.",
                "%d명이서 가는 여행이니까 의견이 다를 수도 있지만, 그래서 오히려 더 흥미진진한 여행이 될 거야!",
                "너희 그룹은 서로 다른 스타일이 조화롭게 섞여있어서 균형 잡힌 여행을 즐길 수 있을 것 같아."
        };

        String[] friendlyRecommendations = {
                "각자 하고 싶은 걸 하나씩 정해서 돌아가면서 즐기는 건 어때? 그러면 모두가 만족할 수 있을 거야.",
                "자유시간과 함께하는 시간을 적절히 나눠서 계획하면 딱 좋을 것 같아!",
                "너무 빡빡하게 짜지 말고 여유롭게 즐기면서, 그때그때 분위기 봐서 결정하는 것도 좋을 것 같아."
        };

        String[] friendlyWarnings = {
                "가장 중요한 건 서로 배려하는 마음! 예산이나 일정 같은 건 미리 얘기해두자.",
                "의견이 다를 때는 서로 이해하려고 노력하고, 타협점을 찾아보면 될 거야.",
                "여행 중에 작은 갈등이 생겨도 금방 풀릴 거야. 다 추억이 될 테니까!"
        };

        int randomIndex = (int)(Math.random() * 3);

        return ComparisonAnalysisDto.builder()
                .userIds(userIds)
                .memberCount(userIds.size())
                .compatibilityScore(baseScore)
                .analysis(String.format(friendlyAnalyses[randomIndex], userIds.size()))
                .recommendations(friendlyRecommendations[randomIndex])
                .warningPoints(friendlyWarnings[randomIndex])
                .build();
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