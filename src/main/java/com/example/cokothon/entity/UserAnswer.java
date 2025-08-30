package com.example.cokothon.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.cokothon.common.entity.BaseEntity;

import java.time.LocalDateTime;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Entity
@Table(name = "useranswer")
public class UserAnswer extends BaseEntity {
	
	@Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long questionId;

    @Column(nullable = false)
    private Integer selectedChoice; // 1, 2, 3
}