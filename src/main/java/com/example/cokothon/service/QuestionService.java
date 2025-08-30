package com.example.cokothon.service;

import com.example.cokothon.entity.Question;
import com.example.cokothon.entity.QuestionCategory;
import com.example.cokothon.repository.QuestionRepository;
import com.example.cokothon.dto.QuestionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;

    /**
     * 모든 질문 조회 (순서대로)
     */
    public List<QuestionDto> getAllQuestions() {
        List<Question> questions = questionRepository.findByIsDeletedFalseOrderByQuestionOrder();
        return questions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리별 질문 조회
     */
    public List<QuestionDto> getQuestionsByCategory(QuestionCategory category) {
        List<Question> questions = questionRepository.findByCategoryOrderByQuestionOrder(category);
        return questions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 특정 질문 조회
     */
    public QuestionDto getQuestionById(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("질문을 찾을 수 없습니다: " + questionId));
        return convertToDto(question);
    }

    /**
     * Question을 QuestionDto로 변환
     */
    private QuestionDto convertToDto(Question question) {
        return QuestionDto.builder()
                .id(question.getId())
                .content(question.getContent())
                .choice1(question.getChoice1())
                .choice2(question.getChoice2())
                .choice3(question.getChoice3())
                .questionOrder(question.getQuestionOrder())
                .category(question.getCategory())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();
    }
}
