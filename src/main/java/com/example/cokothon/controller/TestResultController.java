package com.example.cokothon.controller;

import com.example.cokothon.service.TestResultService;
import com.example.cokothon.dto.TestResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "테스트 결과 관리", description = "여행 성향 테스트 결과 조회 API")
@RestController
@RequestMapping("/api/test-results")
@RequiredArgsConstructor
public class TestResultController {

    private final TestResultService testResultService;

    @Operation(summary = "사용자별 테스트 결과 조회", description = "특정 사용자의 최신 테스트 결과를 조회합니다.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<TestResultDto> getUserTestResult(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long userId) {
        TestResultDto result = testResultService.getUserTestResult(userId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "여러 사용자 테스트 결과 조회", description = "여러 사용자의 테스트 결과를 한번에 조회합니다.")
    @GetMapping("/users")
    public ResponseEntity<List<TestResultDto>> getUsersTestResults(
            @Parameter(description = "사용자 ID 목록", example = "1,2,3")
            @RequestParam List<Long> userIds) {
        List<TestResultDto> results = testResultService.getUsersTestResults(userIds);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "성향별 사용자 조회", description = "특정 성향 타입의 사용자들을 조회합니다.")
    @GetMapping("/personality/{dominantType}")
    public ResponseEntity<List<TestResultDto>> getUsersByPersonality(
            @Parameter(description = "주요 성향 타입", example = "계획형 절약러")
            @PathVariable String dominantType) {
        List<TestResultDto> results = testResultService.getUsersByDominantType(dominantType);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "테스트 완료 여부 확인", description = "사용자가 테스트를 완료했는지 확인합니다.")
    @GetMapping("/completed/{userId}")
    public ResponseEntity<Boolean> hasUserCompletedTest(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long userId) {
        boolean completed = testResultService.hasUserCompletedTest(userId);
        return ResponseEntity.ok(completed);
    }
}
