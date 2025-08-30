package com.example.cokothon.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import com.example.cokothon.common.entity.BaseEntity;

@Schema(description = "여행 성향 테스트 결과")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "test_results")
public class TestResult extends BaseEntity {

    @Schema(description = "테스트를 완료한 사용자 ID", example = "1")
    @Column(nullable = false)
    private Long userId;

    @Schema(description = "계획 성향 점수 (4-12점, 높을수록 계획형)", example = "8", minimum = "4", maximum = "12")
    @Column(nullable = false)
    private Integer planningScore;

    @Schema(description = "예산 성향 점수 (4-12점, 높을수록 럭셔리형)", example = "6", minimum = "4", maximum = "12")
    @Column(nullable = false)
    private Integer budgetScore;

    @Schema(description = "활동 성향 점수 (4-12점, 높을수록 액티브형)", example = "10", minimum = "4", maximum = "12")
    @Column(nullable = false)
    private Integer activityScore;

    @Schema(description = "사교 성향 점수 (4-12점, 높을수록 사교형)", example = "7", minimum = "4", maximum = "12")
    @Column(nullable = false)
    private Integer socialScore;

    @Schema(description = "계획 성향 타입 (6점 기준)", example = "계획형", allowableValues = {"즉흥형", "계획형"})
    @Column(nullable = false)
    private String planningType;

    @Schema(description = "예산 성향 타입 (6점 기준)", example = "절약형", allowableValues = {"절약형", "럭셔리형"})
    @Column(nullable = false)
    private String budgetType;

    @Schema(description = "활동 성향 타입 (6점 기준)", example = "액티브형", allowableValues = {"휴식형", "액티브형"})
    @Column(nullable = false)
    private String activityType;

    @Schema(description = "사교 성향 타입 (6점 기준)", example = "사교형", allowableValues = {"개인형", "사교형"})
    @Column(nullable = false)
    private String socialType;

    @Schema(description = "주요 성향 조합", example = "계획형 절약러")
    @Column(nullable = false)
    private String dominantType;
}