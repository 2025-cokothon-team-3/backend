package com.example.cokothon.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;

import com.example.cokothon.common.response.ApiResponse;
import com.example.cokothon.dto.LoginRequest;
import com.example.cokothon.entity.User;
import com.example.cokothon.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {

    private final UserRepository userRepository;

    @PostMapping("/login")
    public ApiResponse<User> login(@RequestBody LoginRequest request) {
        String nickname = request.getNickname();

        // 1. 비어있는지 확인
        if (nickname == null || nickname.trim().isEmpty()) {
            return ApiResponse.failure("닉네임은 비어있을 수 없습니다.", null);
        }

        // 2. 길이 확인
        if (nickname.length() > 15) {
            return ApiResponse.failure("닉네임은 15자 이하만 가능합니다.", null);
        }

        // 3. 중복 확인
        if (userRepository.existsByName(nickname)) {
            return ApiResponse.failure("이미 존재하는 닉네임입니다.", null);
        }

        // 4. 저장 (정상 케이스)
        User newUser = User.builder()
                .nickname(nickname)
                .build();

        userRepository.save(newUser);

        return ApiResponse.success("로그인 성공", newUser);
    }
}