package com.example.cokothon.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.cokothon.common.entity.BaseEntity;

import java.time.LocalDateTime;

@SuperBuilder
@Getter
@Entity
@Table(name = "answers")
public class Answer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "test_id", foreignKey = @ForeignKey(name = "fk_answer_test"))
    private Question problem;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;  // answer 내용
}