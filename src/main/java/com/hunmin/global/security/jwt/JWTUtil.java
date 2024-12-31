package com.hunmin.global.security.jwt;

import com.hunmin.domain.member.entity.MemberRole;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// JWT 토큰 생성, 토큰에서 이메일과 역할 정보 추출, 토큰의 만료 여부 확인
@Component
public class JWTUtil {

    private SecretKey secretKey;

    // application.properties에 저장된 비밀 키 기반으로 JWT 유틸리티 초기화
    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey =
                new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // JWT 토큰에서 email 정보 추출
    public String getEmail(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    // JWT 토큰에서 role 정보 추출
    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    // JWT 토큰 만료 여부 확인
    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }
    
    // 토큰 구분을 위한 카테고리
    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    // 이메일과 역할 정보 기반으로 JWT 토큰 생성
    public String createJwt(String category, String email, MemberRole role, Long expiredMs) {
        return Jwts.builder()
                .claim("category", category)
                .claim("email", email)
                .claim("role", "ROLE_" + role.name())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }
}
