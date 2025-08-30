package com.example.cokothon.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import com.example.cokothon.common.entity.BaseEntity;

@Schema(description = "여행 성향 비교 분석 결과")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "comparison_results")
public class ComparisonResult extends BaseEntity {

    @Schema(description = "비교 대상 사용자 ID 목록 (콤마 구분)", example = "1,2,3,4")
    @Column(nullable = false)
    private String userIds;

    @Schema(description = "Gemini AI에서 생성된 여행 호환성 분석 피드백",
            example = "이 그룹은 계획형과 즉흥형이 적절히 섞여있어 균형 잡힌 여행이 가능합니다...")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String aiFeedback;

    @Schema(description = "그룹 여행 호환성 점수 (1-100점)", example = "78", minimum = "1", maximum = "100")
    @Column(nullable = false)
    private Integer compatibilityScore;

    @Schema(description = "AI 추천 여행 스타일",
            example = "반계획 반즉흥 스타일의 도시 여행을 추천드립니다.")
    @Column(columnDefinition = "TEXT")
    private String recommendations;

    @Schema(description = "함께 여행할 때 주의해야 할 점들",
            example = "예산 차이로 인한 갈등 가능성이 있으니 미리 예산 범위를 조율하세요.")
    @Column(columnDefinition = "TEXT")
    private String warningPoints;
}