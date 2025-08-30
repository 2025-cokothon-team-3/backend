package com.example.cokothon.controller;

import com.example.cokothon.common.response.ApiResponse;
import com.example.cokothon.dto.ComparisonAnalysisDto;
import com.example.cokothon.dto.ComparisonRequestDto;
import com.example.cokothon.service.TravelComparisonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "여행 성향 비교", description = "그룹 여행 호환성 분석 API")
@RestController
@RequestMapping("/api/travel-comparison")
@RequiredArgsConstructor
@Slf4j
public class TravelComparisonController {

    private final TravelComparisonService comparisonService;

    @Operation(summary = "그룹 여행 호환성 분석",
            description = "2-8명의 그룹 여행 호환성을 AI로 분석하고 추천사항을 제공합니다.")
    @PostMapping("/analyze")
    public ApiResponse<ComparisonAnalysisDto> analyzeGroupCompatibility(
            @Valid @RequestBody ComparisonRequestDto request) {

        try {
            log.info("그룹 호환성 분석 요청: {}", request.getUserIds());

            ComparisonAnalysisDto result = comparisonService.analyzeGroupCompatibility(request.getUserIds());

            String message = String.format("%d명 그룹 분석 완료 (호환성: %d점)",
                    result.getMemberCount(), result.getCompatibilityScore());

            return ApiResponse.success(message, result);

        } catch (IllegalArgumentException e) {
            return ApiResponse.failure(e.getMessage());
        } catch (Exception e) {
            log.error("그룹 호환성 분석 중 오류: ", e);
            return ApiResponse.failure("분석 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    @Operation(summary = "빠른 호환성 체크",
            description = "간단한 호환성 점수만 빠르게 확인합니다.")
    @GetMapping("/quick-check")
    public ApiResponse<Integer> quickCompatibilityCheck(
            @Parameter(description = "사용자 ID 목록 (콤마 구분)", example = "1,2,3")
            @RequestParam List<Long> userIds) {

        try {
            if (userIds.size() < 2 || userIds.size() > 8) {
                return ApiResponse.failure("2명 이상 8명 이하의 사용자만 확인 가능합니다.");
            }

            // 간단한 호환성 점수만 계산 (AI 호출 없이)
            int score = comparisonService.calculateQuickCompatibilityScore(userIds);

            return ApiResponse.success("호환성 체크 완료", score);

        } catch (Exception e) {
            log.error("빠른 호환성 체크 중 오류: ", e);
            return ApiResponse.failure("체크 중 오류가 발생했습니다.");
        }
    }

    @Operation(summary = "사용자 분석 이력",
            description = "특정 사용자가 참여한 모든 그룹 분석 이력을 조회합니다.")
    @GetMapping("/history/{userId}")
    public ApiResponse<List<ComparisonAnalysisDto>> getUserAnalysisHistory(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long userId) {

        try {
            List<ComparisonAnalysisDto> history = comparisonService.getUserAnalysisHistory(userId);

            String message = history.isEmpty() ?
                    "분석 이력이 없습니다." :
                    String.format("%d건의 분석 이력을 찾았습니다.", history.size());

            return ApiResponse.success(message, history);

        } catch (Exception e) {
            log.error("분석 이력 조회 중 오류: ", e);
            return ApiResponse.failure("이력 조회 중 오류가 발생했습니다.");
        }
    }

    @Operation(summary = "인기 조합 분석",
            description = "최근 분석된 인기 그룹 조합들을 확인합니다.")
    @GetMapping("/popular-combinations")
    public ApiResponse<List<String>> getPopularCombinations() {

        try {
            List<String> combinations = comparisonService.getPopularCombinations();
            return ApiResponse.success("인기 조합 조회 완료", combinations);

        } catch (Exception e) {
            log.error("인기 조합 조회 중 오류: ", e);
            return ApiResponse.failure("조회 중 오류가 발생했습니다.");
        }
    }
}