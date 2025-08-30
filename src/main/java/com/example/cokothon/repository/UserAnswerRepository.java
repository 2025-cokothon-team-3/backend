package com.example.cokothon.repository;

import com.example.cokothon.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {

    /**
     * 사용자의 모든 답변 조회 (삭제되지 않은 것만)
     */
    List<UserAnswer> findByUserIdAndIsDeletedFalseOrderByQuestionId(Long userId);

    /**
     * 특정 사용자의 특정 질문 답변 조회
     */
    Optional<UserAnswer> findByUserIdAndQuestionIdAndIsDeletedFalse(Long userId, Long questionId);

    /**
     * 답변 ID로 조회 (삭제되지 않은 것만)
     */
    Optional<UserAnswer> findByIdAndIsDeletedFalse(Long id);

    /**
     * 사용자가 답변한 질문 수 조회
     */
    Long countByUserIdAndIsDeletedFalse(Long userId);

    /**
     * 사용자의 모든 답변 삭제 (논리 삭제)
     */
    List<UserAnswer> findByUserIdAndIsDeletedFalse(Long userId);

    /**
     * 특정 질문에 대한 모든 답변 조회
     */
    List<UserAnswer> findByQuestionIdAndIsDeletedFalse(Long questionId);

    /**
     * 사용자가 모든 질문에 답변했는지 확인 (16개 질문 기준)
     */
    default boolean hasUserCompletedAllQuestions(Long userId) {
        return countByUserIdAndIsDeletedFalse(userId) >= 16;
    }
}