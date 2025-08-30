package com.example.cokothon.controller;

import com.example.cokothon.common.response.ApiResponse;
import com.example.cokothon.service.UserAnswerService;
import com.example.cokothon.dto.UserAnswerRequestDto;
import com.example.cokothon.dto.UserAnswerDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "사용자 답변", description = "여행 성향 테스트 답변 관리 API")
@RestController
@RequestMapping("/api/user-answers")
@RequiredArgsConstructor
@Slf4j
public class UserAnswerController {

    private final UserAnswerService userAnswerService;

    @Operation(summary = "답변 저장", description = "사용자가 선택한 답변을 저장합니다.")
    @PostMapping
    public ApiResponse<UserAnswerDto> saveUserAnswer(@Valid @RequestBody UserAnswerRequestDto request) {
        try {
            log.info("답변 저장 요청: userId={}, questionId={}, selectedChoice={}",
                    request.getUserId(), request.getQuestionId(), request.getSelectedChoice());

            UserAnswerDto savedAnswer = userAnswerService.saveUserAnswer(request);
            return ApiResponse.success("답변이 저장되었습니다.", savedAnswer);

        } catch (IllegalArgumentException e) {
            return ApiResponse.failure(e.getMessage());
        } catch (Exception e) {
            log.error("답변 저장 중 오류: ", e);
            return ApiResponse.failure("답변 저장 중 오류가 발생했습니다.");
        }
    }

    @Operation(summary = "사용자 답변 조회", description = "특정 사용자의 모든 답변을 조회합니다.")
    @GetMapping("/user/{userId}")
    public ApiResponse<List<UserAnswerDto>> getUserAnswers(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long userId) {
        try {
            List<UserAnswerDto> answers = userAnswerService.getUserAnswers(userId);
            return ApiResponse.success("사용자 답변 조회가 완료되었습니다.", answers);

        } catch (Exception e) {
            log.error("사용자 답변 조회 중 오류: ", e);
            return ApiResponse.failure("답변 조회 중 오류가 발생했습니다.");
        }
    }

    @Operation(summary = "답변 수정", description = "기존 답변을 수정합니다.")
    @PutMapping("/{answerId}")
    public ApiResponse<UserAnswerDto> updateUserAnswer(
            @Parameter(description = "답변 ID", example = "1")
            @PathVariable Long answerId,
            @Valid @RequestBody UserAnswerRequestDto request) {
        try {
            UserAnswerDto updatedAnswer = userAnswerService.updateUserAnswer(answerId, request);
            return ApiResponse.success("답변이 수정되었습니다.", updatedAnswer);

        } catch (IllegalArgumentException e) {
            return ApiResponse.failure(e.getMessage());
        } catch (Exception e) {
            log.error("답변 수정 중 오류: ", e);
            return ApiResponse.failure("답변 수정 중 오류가 발생했습니다.");
        }
    }

    @Operation(summary = "사용자 테스트 진행 상황", description = "사용자의 테스트 완료 상황을 조회합니다.")
    @GetMapping("/user/{userId}/progress")
    public ApiResponse<Long> getUserTestProgress(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long userId) {
        try {
            Long answerCount = userAnswerService.getUserAnswerCount(userId);
            return ApiResponse.success("테스트 진행 상황 조회 완료", answerCount);

        } catch (Exception e) {
            log.error("테스트 진행 상황 조회 중 오류: ", e);
            return ApiResponse.failure("조회 중 오류가 발생했습니다.");
        }
    }
}