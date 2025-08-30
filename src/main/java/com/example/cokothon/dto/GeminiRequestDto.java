package com.example.cokothon.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//요청 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeminiRequestDto {
 private List<Content> contents;
 private GenerationConfig generationConfig;
 
 @Data
 @Builder
 @NoArgsConstructor
 @AllArgsConstructor
 public static class Content {
     private List<Part> parts;
 }
 
 @Data
 @Builder
 @NoArgsConstructor
 @AllArgsConstructor
 public static class Part {
     private String text;
 }
 
 @Data
 @Builder
 @NoArgsConstructor
 @AllArgsConstructor
 public static class GenerationConfig {
     private Double temperature;
     private Integer topK;
     private Double topP;
     private Integer maxOutputTokens;
 }
}