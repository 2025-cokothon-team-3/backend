package com.example.cokothon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "테스트 제출 요청 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitTestRequestDto {

    @Schema(description = "사용자 답변 목록")
    private List<UserAnswerDto> answers;

    @Schema(description = "사용자 답변 DTO")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserAnswerDto {

        @Schema(description = "질문 ID", example = "1")
        private Long questionId;

        @Schema(description = "선택한 답변 번호", example = "2")
        private Integer selectedChoice;
    }
}