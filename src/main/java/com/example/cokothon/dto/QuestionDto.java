package com.example.cokothon.dto;

import com.example.cokothon.entity.QuestionCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "질문 정보 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {

    @Schema(description = "질문 ID", example = "1")
    private Long id;

    @Schema(description = "질문 내용", example = "여행 계획을 세울 때 나는?")
    private String content;

    @Schema(description = "선택지 1 (즉흥/절약/휴식/개인 성향)",
            example = "숙소랑 비행기만 예약하면 끝! 나머지는 그날 기분에 따라 생각하지 뭐 ㅋ")
    private String choice1;

    @Schema(description = "선택지 2 (중간 성향)",
            example = "꼭 필요한 예약이랑 가고 싶은 곳 정도는 정리해 둘까?")
    private String choice2;

    @Schema(description = "선택지 3 (계획/럭셔리/액티브/사교 성향)",
            example = "여행을 망칠 수는 없지.. 엑셀에 분 단위로 계획 철저히 세워야지")
    private String choice3;

    @Schema(description = "질문 순서", example = "1", minimum = "1", maximum = "16")
    private Integer questionOrder;

    @Schema(description = "질문 카테고리", example = "PLANNING")
    private QuestionCategory category;

    @Schema(description = "질문 생성일시", example = "2025-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "질문 수정일시", example = "2025-01-15T10:30:00")
    private LocalDateTime updatedAt;
}