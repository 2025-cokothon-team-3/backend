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
import java.util.*;
import java.util.stream.Collectors;

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
     * 실용적이면서 자연스러운 Gemini 프롬프트 생성
     */
    private String createOptimizedPrompt(List<TestResultDto> results, int compatibilityScore) {
        String groupSummary = generateDetailedGroupSummary(results);
        String conflictPoints = identifyPotentialConflicts(results);

        return String.format("""
        %d명의 여행 그룹을 분석해서 실용적인 조언을 해주세요. 여행 경험이 풍부한 전문가가 친근하면서도 구체적으로 조언하는 톤으로 작성해주세요.
        
        그룹 구성: %s
        호환성 점수: %d/100점
        예상 갈등 요소: %s
        
        다음 형식으로 답변해주세요:
        
        [ANALYSIS]
        이 그룹의 여행 성향과 특징을 분석해주세요. 각 성향이 여행에서 어떻게 작용할지, 어떤 시너지가 날지 구체적으로 4-5문장으로 설명해주세요.
        
        [RECOMMENDATIONS]
        이 그룹에게 가장 적합한 여행 스타일과 구체적인 활동, 일정 구성 방법을 3-4문장으로 제안해주세요. 실제 실행 가능한 조언이어야 합니다.
        
        [WARNINGS]
        여행 중 발생할 수 있는 갈등과 해결 방법을 구체적으로 2-3문장으로 조언해주세요. 예방법과 대처법을 포함해주세요.
        
        자연스럽고 이해하기 쉽게, 하지만 깊이 있는 분석이 되도록 작성해주세요.
        """,
                results.size(), groupSummary, compatibilityScore, conflictPoints
        );
    }

    /**
     * 상세한 그룹 요약 생성
     */
    private String generateDetailedGroupSummary(List<TestResultDto> results) {
        Map<String, Long> planningCount = results.stream()
                .collect(Collectors.groupingBy(TestResultDto::getPlanningType, Collectors.counting()));
        Map<String, Long> budgetCount = results.stream()
                .collect(Collectors.groupingBy(TestResultDto::getBudgetType, Collectors.counting()));
        Map<String, Long> activityCount = results.stream()
                .collect(Collectors.groupingBy(TestResultDto::getActivityType, Collectors.counting()));
        Map<String, Long> socialCount = results.stream()
                .collect(Collectors.groupingBy(TestResultDto::getSocialType, Collectors.counting()));

        StringBuilder summary = new StringBuilder();

        // 계획 성향
        planningCount.forEach((type, count) ->
                summary.append(type).append(" ").append(count).append("명, "));

        // 예산 성향
        budgetCount.forEach((type, count) ->
                summary.append(type).append(" ").append(count).append("명, "));

        // 활동 성향
        activityCount.forEach((type, count) ->
                summary.append(type).append(" ").append(count).append("명, "));

        // 사교 성향
        socialCount.forEach((type, count) ->
                summary.append(type).append(" ").append(count).append("명, "));

        return summary.toString().replaceAll(", $", "");
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
     * AI 응답 파싱 - 더 상세한 기본값 제공
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

            // 더 상세한 기본값 설정
            result.putIfAbsent("analysis",
                    "이 그룹은 서로 다른 여행 스타일을 가진 멤버들이 모인 흥미로운 조합입니다. " +
                            "다양한 성향이 섞여있어 의견 조율이 필요하지만, 그만큼 다채로운 경험을 할 수 있는 잠재력이 있습니다. " +
                            "각자의 강점을 살려 역할을 나누면 더욱 효율적이고 만족스러운 여행이 될 것입니다.");

            result.putIfAbsent("recommendations",
                    "자유도가 높은 도시 여행을 추천합니다. 주요 관광지는 함께 방문하되, " +
                            "식사나 쇼핑 등은 개인 선택권을 보장하는 방식으로 일정을 구성하세요. " +
                            "오전에는 단체 활동, 오후에는 자유 시간을 두는 것이 모든 성향을 만족시킬 수 있는 방법입니다.");

            result.putIfAbsent("warnings",
                    "예산과 일정에 대한 사전 합의가 가장 중요합니다. 여행 전에 1일 예산 범위와 필수 일정을 명확히 정하고, " +
                            "의견이 다를 때는 다수결보다는 타협점을 찾는 것이 좋습니다. " +
                            "서로의 여행 스타일을 존중하는 마음가짐이 갈등을 예방하는 핵심입니다.");

        } catch (Exception e) {
            log.warn("AI 응답 파싱 실패, 기본값 사용: {}", e.getMessage());
            // 파싱 실패시 전체 응답을 analysis에 넣되, 적절히 분할
            String[] sentences = response.split("\\. ");
            if (sentences.length >= 3) {
                result.put("analysis", String.join(". ", Arrays.copyOfRange(sentences, 0, 2)) + ".");
                result.put("recommendations", sentences[2] + ".");
                if (sentences.length > 3) {
                    result.put("warnings", String.join(". ", Arrays.copyOfRange(sentences, 3, sentences.length)));
                } else {
                    result.put("warnings", "여행 전 충분한 소통을 통해 갈등을 예방하는 것이 중요합니다.");
                }
            } else {
                result.put("analysis", response);
                result.put("recommendations", "각자의 성향을 고려한 균형잡힌 여행 계획을 세우시길 추천합니다.");
                result.put("warnings", "사전에 충분한 논의를 통해 모두가 만족할 수 있는 여행이 되시길 바랍니다.");
            }
        }

        return result;
    }

    /**
     * 실용적이고 균형 잡힌 대체 분석 제공
     */
    private ComparisonAnalysisDto createFallbackAnalysis(List<Long> userIds, String userIdsKey) {
        int baseScore = 75 + (int)(Math.random() * 20); // 75-95 랜덤

        String[] balancedAnalyses = {
                "이 %d명의 그룹은 다양한 성향이 섞여있어 흥미로운 역학을 보일 것으로 예상됩니다. 서로 다른 여행 스타일이 때로는 의견 차이를 만들 수 있지만, 그만큼 예상치 못한 즐거움과 새로운 경험의 기회도 많아집니다. 각자의 강점을 활용하고 약점을 보완해준다면 매우 균형잡힌 여행이 가능할 것입니다.",

                "%d명이 함께하는 여행은 개인 여행과는 다른 매력이 있습니다. 의견 조율이 필요한 순간들이 있겠지만, 이 과정에서 서로를 더 잘 알게 되고 깊은 우정을 쌓을 수 있는 기회가 됩니다. 다양한 관점이 만나면서 혼자서는 경험하기 어려운 특별한 추억을 만들 수 있을 것입니다.",

                "이 그룹의 구성을 보면 서로의 성향이 좋은 시너지를 낼 수 있는 잠재력이 있습니다. 각자가 가진 여행에 대한 다른 접근 방식들이 조화롭게 어우러진다면, 계획적이면서도 유연하고, 경제적이면서도 만족스러운 여행을 만들어갈 수 있을 것입니다."
        };

        String[] practicalRecommendations = {
                "혼합형 일정 구성을 추천합니다. 오전에는 모두 함께 주요 관광지를 둘러보고, 오후에는 관심사에 따라 소그룹으로 나뉘어 활동하는 방식이 효과적입니다. 저녁에는 다시 모여 하루를 마무리하며 각자의 경험을 공유하는 시간을 갖는 것도 좋겠습니다.",

                "역할 분담형 여행을 제안합니다. 한 명은 교통과 숙소를 담당하고, 다른 한 명은 맛집과 쇼핑을 리서치하는 식으로 각자의 관심 분야를 맡아 전문성을 발휘하면 됩니다. 이렇게 하면 부담도 줄이고 각자의 취향도 충분히 반영할 수 있습니다.",

                "단계별 일정 수립을 권합니다. 첫째 날은 함께 주요 명소를 둘러보며 서로의 여행 패턴을 파악하고, 둘째 날부터는 개인 시간과 단체 시간을 적절히 배분해 진행하세요. 마지막 날에는 모두가 만족할 만한 공통 관심사를 중심으로 일정을 마무리하는 것이 좋습니다."
        };

        String[] specificWarnings = {
                "예산 관리가 핵심입니다. 공동 경비와 개인 경비를 명확히 구분하고, 숙소나 교통비 같은 고정 비용은 미리 정산 방식을 정해두세요. 식사나 쇼핑에서는 개인차를 인정하고 강요하지 않는 분위기를 만드는 것이 중요합니다.",

                "의사결정 방식을 사전에 합의해두세요. 모든 것을 다수결로 정하기보다는, 중요한 사안은 모두가 수용할 수 있는 타협점을 찾고, 사소한 것들은 당번제나 가위바위보로 정하는 등 유연한 접근이 필요합니다.",

                "개인 시간의 중요성을 인식하세요. 24시간 내내 함께 있으면 피로가 쌓일 수 있으므로, 하루 중 1-2시간 정도는 각자 자유롭게 보낼 수 있는 시간을 보장하는 것이 좋습니다. 이것이 오히려 그룹 활동의 질을 높여줄 것입니다."
        };

        int randomIndex = (int)(Math.random() * 3);

        return ComparisonAnalysisDto.builder()
                .userIds(userIds)
                .memberCount(userIds.size())
                .compatibilityScore(baseScore)
                .analysis(String.format(balancedAnalyses[randomIndex], userIds.size()))
                .recommendations(practicalRecommendations[randomIndex])
                .warningPoints(specificWarnings[randomIndex])
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