package com.example.cokothon.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import com.example.cokothon.common.entity.BaseEntity;

@Schema(description = "여행 성향 타입 상세 정보")
@SuperBuilder
@Getter
@NoArgsConstructor
@Entity
@Table(name = "travel_personalities")
public class TravelPersonality extends BaseEntity {

    @Schema(description = "성향 코드", example = "PLANNER",
            allowableValues = {"PLANNER", "SPONTANEOUS", "BUDGET", "LUXURY", "ACTIVE", "RELAXED", "SOCIAL", "INDIVIDUAL"})
    @Column(nullable = false, unique = true)
    private String code;

    @Schema(description = "성향 이름", example = "계획형",
            allowableValues = {"계획형", "즉흥형", "절약형", "럭셔리형", "액티브형", "휴식형", "사교형", "개인형"})
    @Column(nullable = false)
    private String name;

    @Schema(description = "성향 상세 설명",
            example = "여행 전 세세한 계획을 세우는 것을 선호하며, 일정에 따라 움직이는 것을 좋아합니다.")
    @Column(columnDefinition = "TEXT")
    private String description;

    @Schema(description = "성향이 속한 카테고리", example = "PLANNING")
    @Enumerated(EnumType.STRING)
    private QuestionCategory category;

    @Schema(description = "성향 아이콘 이미지 URL", example = "https://example.com/icons/planner.png")
    @Column
    private String iconUrl;

    @Schema(description = "성향 대표 색상 코드", example = "#FF5722")
    @Column
    private String colorCode;
}