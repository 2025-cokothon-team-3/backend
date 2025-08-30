package com.example.cokothon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "성향 비교 요청 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComparisonRequestDto {

    @Schema(description = "비교할 사용자 ID 목록", example = "[1, 2, 3, 4]")
    private List<Long> userIds;
}
