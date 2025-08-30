package com.example.cokothon.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//응답 DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeminiResponseDto {
 private List<Candidate> candidates;
 private UsageMetadata usageMetadata;
 
 @Data
 @NoArgsConstructor
 @AllArgsConstructor
 public static class Candidate {
     private Content content;
     private String finishReason;
     private Integer index;
 }
 
 @Data
 @NoArgsConstructor
 @AllArgsConstructor
 public static class Content {
     private List<Part> parts;
 }
 
 @Data
 @NoArgsConstructor
 @AllArgsConstructor
 public static class Part {
     private String text;
 }
 
 @Data
 @NoArgsConstructor
 @AllArgsConstructor
 public static class UsageMetadata {
     private Integer promptTokenCount;
     private Integer candidatesTokenCount;
     private Integer totalTokenCount;
 }
}