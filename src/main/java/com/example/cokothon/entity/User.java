package com.example.cokothon.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.cokothon.common.entity.BaseEntity;

import java.time.LocalDateTime;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity {
	
	@Column(length = 15, nullable = false, unique = true)
    private String nickname;

    private String travelType; // 테스트 결과 (JSON 문자열)
}