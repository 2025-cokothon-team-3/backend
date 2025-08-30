package com.example.cokothon.controller;
import org.springframework.web.bind.annotation.*;

import com.example.cokothon.common.response.ApiResponse;
import com.example.cokothon.dto.LoginRequest;
import com.example.cokothon.entity.User;
import com.example.cokothon.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	
    @PostMapping("/login")
    public ApiResponse<User> login(@RequestBody LoginRequest request) {
        String nickname = request.getNickname();
        
        return userService.loginCheck(nickname);
    }
}