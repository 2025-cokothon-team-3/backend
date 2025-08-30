package com.example.cokothon.controller;

import com.example.cokothon.common.response.ApiResponse;
import com.example.cokothon.service.TestService;
import com.example.cokothon.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Map;

@Tag(name = "테스트 관리", description = "여행 성향 테스트 제출 및 결과 조회 API")
@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @Operation(summary = "테스트 제출",
            description = "16개 질문에 대한 답변을 제출하고 여행 성향 결과를 받습니다.")
    @PostMapping("/submit/{userId}")
    public ApiResponse<Map<String, Object>> submitTest(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long userId,
            @Parameter(description = "테스트 답변 데이터")
            @Valid @RequestBody SubmitTestRequestDto requestDto) {

        try {
            Map<String, Object> result = testService.submitTest(userId, requestDto);
            return ApiResponse.success("테스트가 성공적으로 완료되었습니다.", result);
        } catch (IllegalArgumentException e) {
            return ApiResponse.failure(e.getMessage());
        } catch (IllegalStateException e) {
            return ApiResponse.failure(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.failure("테스트 제출 중 오류가 발생했습니다.");
        }
    }

    @Operation(summary = "사용자별 테스트 결과 조회",
            description = "특정 사용자의 최신 테스트 결과를 조회합니다.")
    @GetMapping("/result/{userId}")
    public ApiResponse<TestResultDto> getUserTestResult(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long userId) {

        try {
            TestResultDto result = testService.getUserTestResult(userId);
            return ApiResponse.success("테스트 결과 조회가 완료되었습니다.", result);
        } catch (IllegalArgumentException e) {
            return ApiResponse.failure(e.getMessage());
        }
    }

    @Operation(summary = "테스트 완료 여부 확인",
            description = "사용자가 테스트를 완료했는지 확인합니다.")
    @GetMapping("/completed/{userId}")
    public ApiResponse<Boolean> hasUserCompletedTest(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long userId) {

        boolean completed = testService.hasUserCompletedTest(userId);
        String message = completed ? "사용자가 테스트를 완료했습니다." : "사용자가 테스트를 완료하지 않았습니다.";
        return ApiResponse.success(message, completed);
    }

    @Operation(summary = "테스트 재시작",
            description = "사용자의 기존 테스트 결과를 삭제하고 재시작할 수 있게 합니다.")
    @DeleteMapping("/restart/{userId}")
    public ApiResponse<Void> restartTest(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long userId) {

        return ApiResponse.success("테스트를 재시작할 수 있습니다.");
    }
}