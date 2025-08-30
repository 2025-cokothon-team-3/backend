package com.example.cokothon.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Schema(description = "공통 기본 엔티티")
@SuperBuilder
@Getter
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Schema(description = "고유 식별자", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "생성일시", example = "2025-01-15T10:30:00")
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2025-01-15T10:30:00")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Schema(description = "삭제 여부", example = "false", defaultValue = "false")
    @Builder.Default
    @Column(nullable = false)
    private Boolean isDeleted = false;

    public void delete() {
        this.isDeleted = true;
    }

    public void restore() {
        this.isDeleted = false;
    }
}