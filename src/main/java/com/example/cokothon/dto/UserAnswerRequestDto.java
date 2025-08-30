package com.example.cokothon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "사용자 답변 저장 요청")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswerRequestDto {

    @Schema(description = "사용자 ID", example = "1", required = true)
    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;

    @Schema(description = "질문 ID", example = "1", required = true)
    @NotNull(message = "질문 ID는 필수입니다.")
    private Long questionId;

    @Schema(description = "선택한 답변 (1-3)", example = "2", required = true)
    @NotNull(message = "선택한 답변은 필수입니다.")
    @Min(value = 1, message = "답변은 1 이상이어야 합니다.")
    @Max(value = 3, message = "답변은 3 이하여야 합니다.")
    private Integer selectedChoice;
}