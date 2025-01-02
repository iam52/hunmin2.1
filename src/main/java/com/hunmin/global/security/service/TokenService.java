package com.hunmin.global.security.service;

import com.hunmin.domain.member.entity.MemberRole;
import com.hunmin.global.exception.ErrorCode;
import com.hunmin.global.security.dto.TokenResponse;
import com.hunmin.global.security.entity.RefreshEntity;
import com.hunmin.global.security.jwt.JWTUtil;
import com.hunmin.global.security.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public TokenResponse reissue(String refreshToken) {
        validateRefreshToken(refreshToken);

        // 토큰에서 유저 정보 get
        String email = jwtUtil.getEmail(refreshToken);
        String role = jwtUtil.getRole(refreshToken).replace("ROLE_", "");

        // 새로운 access & refresh token 발급
        String newAccess = jwtUtil.createJwt("access", email, MemberRole.valueOf(role), 6000000L); // 100분
        String newRefresh = jwtUtil.createJwt("refresh", email, MemberRole.valueOf(role), 86400000L); // 24시간

        // Refresh Token 저장 DB에 기존 Refresh Token 삭제 후 새 Refresh Token 저장
        refreshRepository.deleteByRefresh(refreshToken);
        saveRefreshToken(email, newRefresh);

        return new TokenResponse(newAccess, newRefresh);
    }

    private void validateRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw ErrorCode.REFRESH_TOKEN_NOT_FOUND.throwException();
        }

        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            throw ErrorCode.REFRESH_TOKEN_EXPIRED.throwException();
        }

        String category = jwtUtil.getCategory(refreshToken);
        if (!category.equals("refresh")) {
            throw ErrorCode.INVALID_REFRESH_TOKEN.throwException();
        }

        if (!refreshRepository.existsByRefresh(refreshToken)) {
            throw ErrorCode.INVALID_REFRESH_TOKEN.throwException();
        }
    }

    private void saveRefreshToken(String email, String refreshToken) {
        RefreshEntity refreshTokenEntity = RefreshEntity.builder()
                .email(email)
                .refresh(refreshToken)
                .expiration(86400000L)
                .build();
        refreshRepository.save(refreshTokenEntity);
    }
}
