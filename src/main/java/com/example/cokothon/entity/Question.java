package com.example.cokothon.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.cokothon.common.entity.BaseEntity;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "tests")
public class Question extends BaseEntity {
   
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String choice1;

    @Column(nullable = false)
    private String choice2;

    @Column(nullable = false)
    private String choice3;

    @Column(nullable = false)
    private Integer questionOrder;
}