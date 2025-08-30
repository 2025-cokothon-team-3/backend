package com.example.cokothon.controller;

import com.example.cokothon.common.response.ApiResponse;
import com.example.cokothon.dto.UserSearchDto;
import com.example.cokothon.dto.LoginRequest;
import com.example.cokothon.entity.User;
import com.example.cokothon.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "사용자 관리", description = "사용자 조회 및 검색 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 로그인", description = "닉네임으로 로그인합니다.")
    @PostMapping("/login")
    public ApiResponse<User> login(@RequestBody LoginRequest request) {
        String nickname = request.getNickname();
        return userService.loginCheck(nickname);
    }

    @Operation(summary = "닉네임으로 사용자 검색", description = "닉네임 키워드로 테스트를 완료한 사용자들을 검색합니다.")
    @GetMapping("/search")
    public ApiResponse<List<UserSearchDto>> searchUsers(
            @Parameter(description = "검색할 닉네임 키워드", example = "여행")
            @RequestParam String keyword) {
        List<UserSearchDto> users = userService.searchUsersByNickname(keyword);
        String message = users.isEmpty() ?
                "검색 결과가 없습니다." :
                users.size() + "명의 사용자를 찾았습니다.";
        return ApiResponse.success(message, users);
    }

    @Operation(summary = "전체 사용자 조회", description = "테스트를 완료한 모든 사용자를 최신순으로 조회합니다.")
    @GetMapping
    public ApiResponse<List<UserSearchDto>> getAllUsers() {
        List<UserSearchDto> users = userService.getAllUsersWithTestResults();
        return ApiResponse.success("전체 사용자 조회가 완료되었습니다.", users);
    }

    @Operation(summary = "특정 사용자 조회", description = "사용자 ID로 특정 사용자 정보를 조회합니다.")
    @GetMapping("/{userId}")
    public ApiResponse<UserSearchDto> getUserById(
            @Parameter(description = "사용자 ID", example = "1")
            @PathVariable Long userId) {
        UserSearchDto user = userService.getUserById(userId);
        return ApiResponse.success("사용자 조회가 완료되었습니다.", user);
    }
}