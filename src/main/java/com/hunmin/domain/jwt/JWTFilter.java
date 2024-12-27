package com.hunmin.domain.jwt;

import com.hunmin.domain.entity.Member;
import com.hunmin.domain.entity.MemberRole;
import com.hunmin.domain.service.MemberService;
import com.hunmin.global.security.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Log4j2
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final MemberService memberService;

    public JWTFilter(JWTUtil jwtUtil, MemberService memberService) {
        this.jwtUtil = jwtUtil;
        this.memberService = memberService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("=== JWTFilter - Request URI: {}", request.getRequestURI());
        log.info("=== JWTFilter - Request Method: {}", request.getMethod());
        log.info("=== JWTFilter - Access Token: {}", request.getHeader("Authorization"));

        // 비밀번호 찾기/변경 관련 요청 필터 제외
        if (request.getRequestURI().startsWith("/api/members/password/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // reissue 엔드포인트는 필터 적용 제외
        if ("/api/members/reissue".equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("*********************");
        log.info(request.getHeader("Authorization"));

        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring(7);
            log.info(accessToken);
            log.info("&&&&&&&&&");
            log.info(jwtUtil.getRole(accessToken));

            // 토큰이 있다면,
            // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
            try {
                jwtUtil.isExpired(accessToken);
            } catch (ExpiredJwtException e) {
                // 만료되면 다음 필터로 넘기지 않고 만료됐다는 메세지 출력
                // response body
                PrintWriter writer = response.getWriter();
                writer.print("===== 액세스 토큰 만료 =====");

                // response status code(프론트와 협의된 응답 코드 반환)
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // 토큰이 만료가 안되었으면,
            // 토큰이 access인지 확인 (발급시 페이로드에 명시)
            String category = jwtUtil.getCategory(accessToken);

            if (!category.equals("access")) {
                // response body
                PrintWriter writer = response.getWriter();
                writer.print("invalid access token");

                // response status code
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // 토큰에서 email, role 값으로 일시적인 세션 생성
            String email = jwtUtil.getEmail(accessToken);
            String role = jwtUtil.getRole(accessToken);

            if (role.startsWith("ROLE_")) {
                role = role.substring(5);
            }

            log.info("=----=-=-=-=-=-=");
            log.info(email);

            Member member = Member.builder()
                    .email(email)
                    .memberRole(MemberRole.valueOf(role))
                    .build();

            CustomUserDetails customUserDetails = new CustomUserDetails(member);

            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
