package com.example.cokothon.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@ConfigurationProperties(prefix = "gemini")
@Data
@Component
public class GeminiConfig {
    private String apiKey;
    private String baseUrl;
}