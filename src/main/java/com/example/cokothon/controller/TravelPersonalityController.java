package com.example.cokothon.controller;

import com.example.cokothon.common.response.ApiResponse;
import com.example.cokothon.entity.TravelPersonality;
import com.example.cokothon.entity.QuestionCategory;
import com.example.cokothon.service.TravelPersonalityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "여행 성향 타입", description = "여행 성향 타입 정보 조회 API")
@RestController
@RequestMapping("/api/personalities")
@RequiredArgsConstructor
public class TravelPersonalityController {

    private final TravelPersonalityService personalityService;

    @Operation(summary = "전체 성향 타입 조회",
            description = "모든 여행 성향 타입을 조회합니다.")
    @GetMapping
    public ApiResponse<List<TravelPersonality>> getAllPersonalities() {
        List<TravelPersonality> personalities = personalityService.getAllPersonalities();
        return ApiResponse.success("성향 타입 조회가 완료되었습니다.", personalities);
    }

    @Operation(summary = "카테고리별 성향 타입 조회",
            description = "특정 카테고리의 성향 타입들을 조회합니다.")
    @GetMapping("/category/{category}")
    public ApiResponse<List<TravelPersonality>> getPersonalitiesByCategory(
            @Parameter(description = "성향 카테고리", example = "PLANNING")
            @PathVariable QuestionCategory category) {
        List<TravelPersonality> personalities = personalityService.getPersonalitiesByCategory(category);
        return ApiResponse.success(category + " 카테고리 성향 타입 조회가 완료되었습니다.", personalities);
    }
}