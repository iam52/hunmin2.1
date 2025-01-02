package com.hunmin.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hunmin.domain.member.dto.LoginResponse;
import com.hunmin.domain.member.entity.MemberRole;
import com.hunmin.global.security.entity.CustomUserDetails;
import com.hunmin.global.security.jwt.JWTUtil;
import com.hunmin.global.security.entity.RefreshEntity;
import com.hunmin.global.security.repository.RefreshRepository;
import com.hunmin.global.security.jwt.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

// 로그인 요청 처리 클래스
@Slf4j
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    // 상수 정의
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 60000000L;
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 86400000L;

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final CookieUtil cookieUtil;
    private final ObjectMapper objectMapper;

    public CustomLoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil,
                             RefreshRepository refreshRepository, CookieUtil cookieUtil, ObjectMapper objectMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.cookieUtil = cookieUtil;
        this.objectMapper = objectMapper;
        setFilterProcessesUrl("/api/members/login");
    }

    // 요청에서 email 파라미터 추출
    @Override
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter("email");
    }

    // 위에서 추출한 이메일과 비밀번호 추출하여 인증 시도
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        log.info("========= attemptAuthentication 시작 =========");
        try {
            Map<String, String> requestMap = objectMapper.readValue(request.getInputStream(), Map.class);
            String email = requestMap.get("email");
            String password = requestMap.get("password");
            log.info("===== 이메일: {} =====", email);
            log.info("===== 비밀번호: {} =====", password);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new AuthenticationServiceException("===== 잘못된 요청 폼 =====");
        }
    }

    // 로그인 성공 시 사용자 정보를 기반으로 JWT 토큰을 생성하고, 이를 Authorization 헤더에 추가
    @Override
    protected void successfulAuthentication(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication)
            throws IOException {
        log.info("========= successfulAuthentication 시작 =========");

        // 유저 정보
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String email = authentication.getName();
        String role = extractRole(authentication);

        log.info("===== Authentication 성공!! email: {}, Role: {}", email, role);

        // 토큰 생성
        String access = jwtUtil.createJwt("access", email, MemberRole.valueOf(role), ACCESS_TOKEN_EXPIRE_TIME); // 100분
        String refresh = jwtUtil.createJwt("refresh", email, MemberRole.valueOf(role), REFRESH_TOKEN_EXPIRE_TIME); // 24시간
        log.info("=== 생성된 access 토큰: {}", access);
        log.info("=== 생성된 refresh 토큰: {}", refresh);

        // refresh 토큰 저장
        addRefreshEntity(email, refresh);

        // 응답 dto 생성
        LoginResponse loginResponse = LoginResponse.builder()
                .token(access)
                .refreshToken(refresh)
                .memberId(customUserDetails.getMemberId())
                .role(role)
                .nickname(customUserDetails.getNickname())
                .image(customUserDetails.getImage())
                .email(email)
                .level(customUserDetails.getLevel())
                .country(customUserDetails.getCountry())
                .build();

        // 응답 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("access", access);
        response.addCookie(CookieUtil.createRefreshTokenCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());

        objectMapper.writeValue(response.getWriter(), loginResponse);
    }

    private String extractRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .map(auth -> auth.replace("ROLE_", ""))
                .orElseThrow(() -> new AuthenticationServiceException("=== Role not found ==="));
    }

    private void addRefreshEntity(String email, String refresh) {
        Date date = new Date(System.currentTimeMillis() + CustomLoginFilter.REFRESH_TOKEN_EXPIRE_TIME);
        RefreshEntity refreshEntity = RefreshEntity.builder()
                .email(email)
                .refresh(refresh)
                .expiration(date.getTime())
                .build();
        refreshRepository.save(refreshEntity);
    }

    // 로그인 실패 시 HTTP 응답 401로 설정(유효한 자격 증명 미제공 시 요청 거부)
    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        log.info("===== 인증 실패 =====");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}
