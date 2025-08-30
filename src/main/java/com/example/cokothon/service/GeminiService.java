package com.example.cokothon.service;

import java.time.Duration;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.cokothon.dto.GeminiRequestDto;
import com.example.cokothon.dto.GeminiResponseDto;
import com.example.cokothon.config.GeminiConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeminiService {
    
    private final GeminiConfig geminiConfig;
    private final WebClient webClient;

    public Mono<String> generateText(String prompt) {
        GeminiRequestDto request = createRequest(prompt);

        String url = geminiConfig.getApi().getBaseUrl() +
                "/models/" + geminiConfig.getApi().getModel() + ":generateContent?key=" +
                geminiConfig.getApi().getKey();
        
        return webClient.post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    return response.bodyToMono(String.class)
                            .flatMap(body -> {
                                log.error("4xx error: {}", body);
                                return Mono.error(new RuntimeException("Client error: " + body));
                            });
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    return response.bodyToMono(String.class)
                            .flatMap(body -> {
                                log.error("5xx error: {}", body);
                                return Mono.error(new RuntimeException("Server error: " + body));
                            });
                })
                .bodyToMono(GeminiResponseDto.class)
                .map(this::extractTextFromResponse)
                .doOnError(error -> log.error("Error calling Gemini API", error));
    }
    
    public String generateTextSync(String prompt) {
        try {
            return generateText(prompt).block(Duration.ofSeconds(30));
        } catch (Exception e) {
            log.error("Error in synchronous call", e);
            throw new RuntimeException("Failed to generate text", e);
        }
    }
    
    private GeminiRequestDto createRequest(String prompt) {
        return GeminiRequestDto.builder()
                .contents(List.of(
                        GeminiRequestDto.Content.builder()
                                .parts(List.of(
                                        GeminiRequestDto.Part.builder()
                                                .text(prompt)
                                                .build()
                                ))
                                .build()
                ))
                .generationConfig(GeminiRequestDto.GenerationConfig.builder()
                        .temperature(0.7)
                        .topK(40)
                        .topP(0.95)
                        .maxOutputTokens(2048)
                        .build())
                .build();
    }
    
    private String extractTextFromResponse(GeminiResponseDto response) {
        if (response.getCandidates() == null || response.getCandidates().isEmpty()) {
            throw new RuntimeException("No candidates in response");
        }
        
        GeminiResponseDto.Candidate candidate = response.getCandidates().get(0);
        if (candidate.getContent() == null || 
            candidate.getContent().getParts() == null || 
            candidate.getContent().getParts().isEmpty()) {
            throw new RuntimeException("No content in response");
        }
        
        return candidate.getContent().getParts().get(0).getText();
    }
}