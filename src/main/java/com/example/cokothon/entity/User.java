package com.example.cokothon.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import com.example.cokothon.common.entity.BaseEntity;

@Schema(description = "사용자 정보")
@SuperBuilder
@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Schema(description = "사용자 닉네임", example = "여행러버123", maxLength = 20)
    @Column(length = 20, nullable = false, unique = true)
    private String nickname;
}