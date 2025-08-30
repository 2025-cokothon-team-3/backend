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
        List<Question> questions = questionRepository.findByCategoryAndIsDeletedFalseOrderByQuestionOrder(category);
        return questions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 특정 질문 조회 (ID 기준)
     */
    public QuestionDto getQuestionById(Long questionId) {
        Question question = questionRepository.findByIdAndIsDeletedFalse(questionId)
                .orElseThrow(() -> new IllegalArgumentException("질문을 찾을 수 없습니다: " + questionId));
        return convertToDto(question);
    }

    /**
     * 질문 순서로 질문 조회 (프론트엔드용)
     * @param questionOrder 질문 순서 (1-16)
     * @return QuestionDto
     */
    public QuestionDto getQuestionByOrder(Integer questionOrder) {
        // 입력값 검증
        if (questionOrder == null || questionOrder < 1 || questionOrder > 16) {
            throw new IllegalArgumentException("질문 순서는 1부터 16까지 입력 가능합니다.");
        }

        Question question = questionRepository.findByQuestionOrderAndIsDeletedFalse(questionOrder)
                .orElseThrow(() -> new IllegalArgumentException("해당 순서의 질문을 찾을 수 없습니다: " + questionOrder));

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

    /**
     * 다음 질문 존재 여부 확인
     */
    public boolean hasNextQuestion(Integer currentOrder) {
        if (currentOrder == null || currentOrder >= 16) {
            return false;
        }

        return questionRepository.findByQuestionOrderAndIsDeletedFalse(currentOrder + 1)
                .isPresent();
    }

    /**
     * 이전 질문 존재 여부 확인
     */
    public boolean hasPreviousQuestion(Integer currentOrder) {
        if (currentOrder == null || currentOrder <= 1) {
            return false;
        }

        return questionRepository.findByQuestionOrderAndIsDeletedFalse(currentOrder - 1)
                .isPresent();
    }

    /**
     * 전체 질문 개수 조회
     */
    public Long getTotalQuestionCount() {
        return questionRepository.findByIsDeletedFalseOrderByQuestionOrder().stream().count();
    }
}