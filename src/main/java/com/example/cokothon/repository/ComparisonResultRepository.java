package com.example.cokothon.repository;

import com.example.cokothon.entity.ComparisonResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComparisonResultRepository extends JpaRepository<ComparisonResult, Long> {

    /**
     * 특정 사용자 ID 조합의 최근 분석 결과 조회
     */
    @Query("SELECT cr FROM ComparisonResult cr " +
            "WHERE cr.userIds = :userIds AND cr.isDeleted = false " +
            "AND cr.createdAt >= :cutoffDate " +
            "ORDER BY cr.createdAt DESC")
    Optional<ComparisonResult> findRecentByUserIds(@Param("userIds") String userIds,
                                                   @Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * 특정 사용자가 포함된 모든 분석 결과 조회
     */
    @Query("SELECT cr FROM ComparisonResult cr " +
            "WHERE cr.userIds LIKE CONCAT('%', :userId, '%') AND cr.isDeleted = false " +
            "ORDER BY cr.createdAt DESC")
    List<ComparisonResult> findByUserIdsContaining(@Param("userId") String userId);

    /**
     * 호환성 점수별 통계 조회
     */
    @Query("SELECT AVG(cr.compatibilityScore) FROM ComparisonResult cr WHERE cr.isDeleted = false")
    Double getAverageCompatibilityScore();

    /**
     * 최근 N일간의 분석 결과
     */
    @Query("SELECT cr FROM ComparisonResult cr " +
            "WHERE cr.createdAt >= :cutoffDate AND cr.isDeleted = false " +
            "ORDER BY cr.createdAt DESC")
    List<ComparisonResult> findRecentResults(@Param("cutoffDate") LocalDateTime cutoffDate);
}