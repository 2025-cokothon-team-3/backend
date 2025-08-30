package com.example.cokothon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Schema(description = "사용자 답변 정보")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswerDto {

    @Schema(description = "답변 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "질문 ID", example = "1")
    private Long questionId;

    @Schema(description = "질문 순서", example = "1")
    private Integer questionOrder;

    @Schema(description = "질문 내용", example = "여행 계획을 세울 때 나는?")
    private String questionContent;

    @Schema(description = "선택한 답변 (1-3)", example = "2")
    private Integer selectedChoice;

    @Schema(description = "선택한 답변 텍스트")
    private String selectedChoiceText;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;
}