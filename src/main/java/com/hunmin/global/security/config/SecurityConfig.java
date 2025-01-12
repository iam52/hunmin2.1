package com.hunmin.global.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hunmin.global.security.filter.CustomLogoutFilter;
import com.hunmin.global.security.jwt.JWTFilter;
import com.hunmin.global.security.jwt.JWTUtil;
import com.hunmin.global.security.filter.CustomLoginFilter;
import com.hunmin.global.security.repository.RefreshRepository;
import com.hunmin.domain.member.service.MemberService;
import com.hunmin.global.security.jwt.CookieUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;

// Spring Security 설정
@Configuration
@EnableWebSecurity
@Log4j2
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true) // securedEnabled => Secured 애노테이션 사용 여부, prePostEnabled => PreAuthorize 어노테이션 사용 여부.
public class SecurityConfig {

    // AuthenticationConfiguration과 JWT 유틸리티 초기화
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final CookieUtil cookieUtil;
    private final ObjectMapper objectMapper;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil,
                          RefreshRepository refreshRepository, CookieUtil cookieUtil, ObjectMapper objectMapper) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.cookieUtil = cookieUtil;
        this.objectMapper = objectMapper;
    }

    // AuthenticationManager를 Bean으로 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // 비밀번호 암호화를 위한 BCryptPasswordEncoder Bean 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // SecurityFilterChain 체인 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MemberService memberService) throws Exception {
        AuthenticationManager authManager = authenticationManager(authenticationConfiguration);
        CustomLoginFilter customLoginFilter = new CustomLoginFilter(authManager, jwtUtil, refreshRepository, cookieUtil, objectMapper);
        customLoginFilter.setFilterProcessesUrl("/api/members/login");
        http
                .cors((corsCustomizer -> corsCustomizer.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                    configuration.setAllowedMethods(Collections.singletonList("*"));
                    configuration.setAllowCredentials(true);
                    configuration.setAllowedHeaders(Collections.singletonList("*"));
                    configuration.setMaxAge(3600L);
                    configuration.setExposedHeaders(Collections.singletonList("Authorization"));
                    return configuration;
                })))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/api/members").permitAll()
                        .requestMatchers("/api/members/login").permitAll()
                        .requestMatchers("/api/members/reissue").permitAll()
                        .requestMatchers("/api/members/admin").hasRole("ADMIN")
                        .requestMatchers("/api/members/password/**").permitAll()
                        .requestMatchers("/webjars/**", "/images/**", "/favicon.ico").permitAll() // 웹 자원 경로 허용
                        .requestMatchers("/api/notification/**").permitAll() // 알림 실시간 반영 위한 수정
                        .requestMatchers("/api/board/uploadImage/**").permitAll() // 게시글 작성 이미지
                        .requestMatchers("uploads/**").permitAll()
                        .requestMatchers("api/members/uploads/**").permitAll() // 프로필 이미지
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll() // swagger
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/api/notices/list/**").permitAll()
                        .anyRequest().permitAll())
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAt(customLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JWTFilter(jwtUtil, memberService), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);
        return http.build();
    }
}