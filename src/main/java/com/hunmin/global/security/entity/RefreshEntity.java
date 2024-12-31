package com.hunmin.global.security.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

// refresh 토큰 저장을 위한 엔티티
@Entity
@Getter
@Setter
@Table(name = "refresh_tokens")
public class RefreshEntity {
    @Id
    @Column(nullable = false, unique = true)
    private String email;

    private String refresh;
    private String expiration;

}
