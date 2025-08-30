package com.example.cokothon.test.controller;

import com.example.cokothon.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping
    public ApiResponse<Map<String, Object>> health() {
        log.info("Health check requested at {}", LocalDateTime.now());

        Map<String, Object> healthInfo = Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now(),
                "service", "cokothon-api",
                "version", "0.0.1-SNAPSHOT"
        );

        return ApiResponse.success("서비스가 정상 동작 중입니다.", healthInfo);
    }
}