package com.example.cokothon.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "여행 성향 질문 카테고리")
public enum QuestionCategory {
    @Schema(description = "계획 성향 (즉흥형 vs 계획형)")
    PLANNING,

    @Schema(description = "예산 성향 (절약형 vs 럭셔리형)")
    BUDGET,

    @Schema(description = "활동 성향 (휴식형 vs 액티브형)")
    ACTIVITY,

    @Schema(description = "사교 성향 (개인형 vs 사교형)")
    SOCIAL
}
