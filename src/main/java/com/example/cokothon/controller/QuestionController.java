package com.example.cokothon.controller;

import com.example.cokothon.common.response.ApiResponse;
import com.example.cokothon.entity.QuestionCategory;
import com.example.cokothon.service.QuestionService;
import com.example.cokothon.dto.QuestionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "질문 관리", description = "여행 성향 테스트 질문 조회 API")
@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @Operation(summary = "전체 질문 조회", description = "16개의 여행 성향 테스트 질문을 순서대로 조회합니다.")
    @GetMapping
    public ApiResponse<List<QuestionDto>> getAllQuestions() {
        List<QuestionDto> questions = questionService.getAllQuestions();
        return ApiResponse.success("전체 질문 조회가 완료되었습니다.", questions);
    }

    @Operation(summary = "카테고리별 질문 조회", description = "특정 카테고리(PLANNING, BUDGET, ACTIVITY, SOCIAL)의 질문들을 조회합니다.")
    @GetMapping("/category/{category}")
    public ApiResponse<List<QuestionDto>> getQuestionsByCategory(
            @Parameter(description = "질문 카테고리", example = "PLANNING")
            @PathVariable QuestionCategory category) {
        List<QuestionDto> questions = questionService.getQuestionsByCategory(category);
        return ApiResponse.success(category + " 카테고리 질문 조회가 완료되었습니다.", questions);
    }

    @Operation(summary = "특정 질문 조회", description = "질문 ID로 특정 질문을 조회합니다.")
    @GetMapping("/{questionId}")
    public ApiResponse<QuestionDto> getQuestionById(
            @Parameter(description = "질문 ID", example = "1")
            @PathVariable Long questionId) {
        QuestionDto question = questionService.getQuestionById(questionId);
        return ApiResponse.success("질문 조회가 완료되었습니다.", question);
    }
}