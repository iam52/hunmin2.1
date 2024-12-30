package com.hunmin.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hunmin.domain.member.entity.MemberLevel;
import com.hunmin.domain.member.entity.MemberRole;
import com.hunmin.global.security.jwt.RefreshEntity;
import com.hunmin.global.security.jwt.JWTUtil;
import com.hunmin.global.security.jwt.RefreshRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

// 로그인 요청 처리 클래스
@Log4j2
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public CustomLoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
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
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> requestMap = mapper.readValue(request.getInputStream(), Map.class);
            String email = requestMap.get("email");
            String password = requestMap.get("password");
            log.info("===== 이메일: " + email + " =====");
            log.info("===== 비밀번호: " + password + " =====");
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);
            log.info("===== 인증 결과: " + authToken + " =====");
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new AuthenticationServiceException("===== 잘못된 요청 폼 =====");
        }
    }

    // 로그인 성공 시 사용자 정보를 기반으로 JWT 토큰을 생성하고, 이를 Authorization 헤더에 추가
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication) throws IOException {
        log.info("========= successfulAuthentication 시작 =========");

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        //유저 정보
        String email = authentication.getName();
        // 사용자 권한 정보 추출하고 "ROLE_" 접두사 제거
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority().replace("ROLE_", "");

        log.info("===== Authentication 성공!! email: {}, Role: {}", email, role);
        // 클라이언트 전송을 위한 추가 사용자 정보 추출
        Long memberId = customUserDetails.getMemberId();
        String nickname = customUserDetails.getNickname();
        String image = "http://localhost:8080" + customUserDetails.getImage();
        MemberLevel level = customUserDetails.getLevel();
        String country = customUserDetails.getCountry();

        //토큰 생성
        String access = jwtUtil.createJwt("access", email, MemberRole.valueOf(role), 6000000L); // 100분
        String refresh = jwtUtil.createJwt("refresh", email, MemberRole.valueOf(role), 86400000L); // 24시간
        log.info("생성된 access 토큰: " + access);
        log.info("생성된 refresh 토큰: " + refresh);
        
        // refresh 토큰 저장
        addRefreshEntity(email, refresh, 86400000L);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"token\": \"" + access + "\"," +
                " \"refreshToken\": \"" + refresh + "\"," +
                " \"memberId\": " + memberId + ", " +
                "\"role\": \"" + role + "\", " +
                "\"nickname\": \"" + nickname + "\", " +
                "\"image\": \"" + image + "\", " +
                "\"email\": \"" + email + "\", " +
                "\"level\": \"" + level + "\", " +
                "\"country\": \"" + country + "\"}");

        //응답 설정
        response.setHeader("access", access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
    }

    private void addRefreshEntity(String email, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setEmail(email);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());
        refreshRepository.save(refreshEntity);
    }

    // 로그인 실패 시 HTTP 응답 401로 설정(유효한 자격 증명 미제공 시 요청 거부)
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) {
        response.setStatus(401);
        log.info("===== 인증 실패 =====");
    }

    private Cookie createCookie(String key, String value) {
        // key와 jwt를 매개로 받아 cookie 생성
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60); // 쿠키 생명 주기
        // cookie.setSecure(true); // https 사용 시 적용
        // cookie.setPath("/"); // 쿠키 적용 범위
        cookie.setHttpOnly(true); // 자바스크립트로 쿠키에 접근 제한
        return cookie;
    }
}
