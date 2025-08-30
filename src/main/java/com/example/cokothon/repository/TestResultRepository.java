package com.example.cokothon.repository;

import com.example.cokothon.entity.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {

    /**
     * 사용자별 테스트 결과 조회 (최신순)
     */
    Optional<TestResult> findTopByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 여러 사용자의 테스트 결과 조회
     */
    List<TestResult> findByUserIdInOrderByCreatedAtDesc(List<Long> userIds);

    /**
     * 특정 성향 타입의 사용자들 조회
     */
    List<TestResult> findByDominantTypeContaining(String dominantType);

    /**
     * 호환성 점수 범위로 조회
     */
    @Query("SELECT tr FROM TestResult tr WHERE tr.userId IN :userIds")
    List<TestResult> findByUserIds(@Param("userIds") List<Long> userIds);
}
