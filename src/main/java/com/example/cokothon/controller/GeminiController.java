package com.example.cokothon.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cokothon.service.GeminiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/gemini")
@RequiredArgsConstructor
@Slf4j
public class GeminiController {
    
    private final GeminiService geminiService;
    
    @PostMapping("/generate")
    public ResponseEntity<Map<String, String>> generateText(@RequestBody Map<String, String> request) {
        try {
            String prompt = request.get("prompt");
            if (prompt == null || prompt.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Prompt is required"));
            }
            
            String response = geminiService.generateTextSync(prompt);
            return ResponseEntity.ok(Map.of("response", response));
            
        } catch (Exception e) {
            log.error("Error generating text", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to generate text: " + e.getMessage()));
        }
    }
    
    @PostMapping("/generate-async")
    public Mono<ResponseEntity<Map<String, String>>> generateTextAsync(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        if (prompt == null || prompt.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(Map.of("error", "Prompt is required")));
        }
        
        return geminiService.generateText(prompt)
                .map(response -> ResponseEntity.ok(Map.of("response", response)))
                .onErrorReturn(ResponseEntity.internalServerError()
                        .body(Map.of("error", "Failed to generate text")));
    }
}