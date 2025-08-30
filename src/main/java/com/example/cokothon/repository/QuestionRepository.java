package com.example.cokothon.repository;

import com.example.cokothon.entity.Question;
import com.example.cokothon.entity.QuestionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * 질문 순서로 정렬하여 모든 질문 조회
     */
    List<Question> findAllByOrderByQuestionOrder();

    /**
     * 카테고리별 질문 조회
     */
    List<Question> findByCategoryOrderByQuestionOrder(QuestionCategory category);

    /**
     * 삭제되지 않은 질문들만 조회
     */
    List<Question> findByIsDeletedFalseOrderByQuestionOrder();
}