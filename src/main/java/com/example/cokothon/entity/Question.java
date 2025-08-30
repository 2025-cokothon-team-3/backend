package com.example.cokothon.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import com.example.cokothon.common.entity.BaseEntity;

@Schema(description = "여행 성향 테스트 질문")
@SuperBuilder
@Getter
@NoArgsConstructor
@Entity
@Table(name = "questions")
public class Question extends BaseEntity {

    @Schema(description = "질문 내용", example = "여행 계획을 세울 때 나는?")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Schema(description = "선택지 1 (즉흥/절약/휴식/개인 성향)",
            example = "숙소랑 비행기만 예약하면 끝! 나머지는 그날 기분에 따라 생각하지 뭐 ㅋ")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String choice1;

    @Schema(description = "선택지 2 (중간 성향)",
            example = "꼭 필요한 예약이랑 가고 싶은 곳 정도는 정리해 둘까?")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String choice2;

    @Schema(description = "선택지 3 (계획/럭셔리/액티브/사교 성향)",
            example = "여행을 망칠 수는 없지.. 엑셀에 분 단위로 계획 철저히 세워야지")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String choice3;

    @Schema(description = "질문 순서", example = "1", minimum = "1", maximum = "16")
    @Column(nullable = false)
    private Integer questionOrder;

    @Schema(description = "질문 카테고리", example = "PLANNING")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionCategory category;
}