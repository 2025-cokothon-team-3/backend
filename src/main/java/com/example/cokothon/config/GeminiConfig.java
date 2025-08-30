package com.example.cokothon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

@ConfigurationProperties(prefix = "gemini")
@Data
@Component
public class GeminiConfig {
    private Api api;

    @Data
    public static class Api {
        private String key;
        private String baseUrl;
        private String model;
    }
}