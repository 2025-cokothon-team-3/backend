package com.example.cokothon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "그룹 여행 호환성 분석 결과")
@Getter
@Builder
public class ComparisonAnalysisDto {

    @Schema(description = "분석 대상 사용자 ID 목록")
    private List<Long> userIds;

    @Schema(description = "그룹 멤버 수", example = "4")
    private int memberCount;

    @Schema(description = "그룹 호환성 점수 (1-100)", example = "78")
    private int compatibilityScore;

    @Schema(description = "AI가 생성한 전체적인 분석 내용")
    private String analysis;

    @Schema(description = "추천 여행 스타일 및 활동")
    private String recommendations;

    @Schema(description = "주의사항 및 갈등 예방 팁")
    private String warningPoints;

    @Schema(description = "분석 생성 일시")
    private LocalDateTime analysisDate;
}
