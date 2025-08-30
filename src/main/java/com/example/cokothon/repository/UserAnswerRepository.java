package com.example.cokothon.repository;

import com.example.cokothon.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {

    /**
     * 사용자별 답변 조회
     */
    List<UserAnswer> findByUserIdOrderByQuestionId(Long userId);

    /**
     * 특정 질문에 대한 모든 답변 조회
     */
    List<UserAnswer> findByQuestionId(Long questionId);

    /**
     * 사용자가 답변한 질문 수 확인
     */
    long countByUserId(Long userId);
}