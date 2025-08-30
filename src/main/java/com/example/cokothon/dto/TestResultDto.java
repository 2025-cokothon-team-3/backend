package com.example.cokothon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "테스트 결과 응답 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestResultDto {

    @Schema(description = "계획 성향 타입", example = "계획형")
    private String planningType;

    @Schema(description = "예산 성향 타입", example = "절약형")
    private String budgetType;

    @Schema(description = "활동 성향 타입", example = "액티브형")
    private String activityType;

    @Schema(description = "사교 성향 타입", example = "사교형")
    private String socialType;

    @Schema(description = "계획 성향 점수", example = "8")
    private Integer planningScore;

    @Schema(description = "예산 성향 점수", example = "6")
    private Integer budgetScore;

    @Schema(description = "활동 성향 점수", example = "10")
    private Integer activityScore;

    @Schema(description = "사교 성향 점수", example = "7")
    private Integer socialScore;

    @Schema(description = "주요 성향 조합", example = "계획형 절약러")
    private String dominantType;
}
