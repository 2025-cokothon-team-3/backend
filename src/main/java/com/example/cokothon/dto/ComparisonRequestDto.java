package com.example.cokothon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(description = "그룹 호환성 분석 요청")
@Getter
@Setter
public class ComparisonRequestDto {

    @Schema(description = "분석할 사용자 ID 목록 (2-8명)", example = "[1, 2, 3, 4]")
    @NotNull(message = "사용자 ID 목록은 필수입니다")
    @Size(min = 2, max = 8, message = "2명 이상 8명 이하의 사용자만 분석 가능합니다")
    private List<Long> userIds;
}