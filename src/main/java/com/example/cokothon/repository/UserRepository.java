package com.example.cokothon.repository;

import com.example.cokothon.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 닉네임으로 사용자 조회
     */
    Optional<User> findByNickname(String nickname);

    /**
     * 닉네임 포함 검색 (삭제되지 않은 사용자만)
     */
    List<User> findByNicknameContainingAndIsDeletedFalse(String keyword);

    /**
     * 닉네임 중복 체크
     */
    boolean existsByNickname(String nickname);

    /**
     * 활성 사용자 목록 조회 (최근 생성 순)
     */
    @Query("SELECT u FROM User u WHERE u.isDeleted = false ORDER BY u.createdAt DESC")
    List<User> findActiveUsersOrderByCreatedAtDesc();

    /**
     * 테스트 완료한 사용자들만 검색
     */
    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN TestResult tr ON u.id = tr.userId " +
            "WHERE u.nickname LIKE %:keyword% AND u.isDeleted = false " +
            "ORDER BY u.createdAt DESC")
    List<User> findUsersWithTestResultsByNickname(@Param("keyword") String keyword);

    boolean existsByName(String name);
}