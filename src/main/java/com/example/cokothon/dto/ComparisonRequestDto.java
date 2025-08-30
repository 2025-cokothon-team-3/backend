package com.example.cokothon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "성향 비교 요청 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComparisonRequestDto {

    @Schema(description = "비교할 사용자 ID 목록", example = "[1, 2, 3, 4]")
    private java.util.List<Long> userIds;
}
