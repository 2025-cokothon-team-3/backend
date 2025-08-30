package com.example.cokothon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "사용자 검색 결과 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchDto {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 닉네임", example = "여행러버123")
    private String nickname;

    @Schema(description = "사용자 테스트 결과")
    private TestResultDto testResult;

    @Schema(description = "사용자 생성일시", example = "2025-01-15T10:30:00")
    private LocalDateTime createdAt;
}
