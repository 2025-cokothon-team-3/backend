package com.example.cokothon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "AI 피드백 응답 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiFeedbackDto {

    @Schema(description = "비교 대상 사용자 목록")
    private List<UserSearchDto> users;

    @Schema(description = "AI가 생성한 호환성 분석 피드백",
            example = "이 그룹은 서로 다른 성향이 조화롭게 섞여있어...")
    private String feedback;

    @Schema(description = "그룹 호환성 점수", example = "78")
    private Integer compatibilityScore;

    @Schema(description = "AI 추천사항 목록")
    private List<String> recommendations;

    @Schema(description = "주의사항 목록")
    private List<String> warningPoints;
}