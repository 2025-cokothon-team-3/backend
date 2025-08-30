package com.example.cokothon.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import com.example.cokothon.common.entity.BaseEntity;

@Schema(description = "사용자 답변 정보")
@SuperBuilder
@Getter
@NoArgsConstructor
@Entity
@Table(name = "user_answers")
public class UserAnswer extends BaseEntity {

    @Schema(description = "답변한 사용자 ID", example = "1")
    @Column(nullable = false)
    private Long userId;

    @Schema(description = "답변한 질문 ID", example = "1")
    @Column(nullable = false)
    private Long questionId;

    @Schema(description = "선택한 답변 번호 (1: 즉흥/절약/휴식/개인, 2: 중간, 3: 계획/럭셔리/액티브/사교)",
            example = "2", minimum = "1", maximum = "3")
    @Column(nullable = false)
    private Integer selectedChoice;
}