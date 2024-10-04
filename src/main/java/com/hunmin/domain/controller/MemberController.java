package com.hunmin.domain.controller;

import com.hunmin.domain.dto.member.MemberDTO;
import com.hunmin.domain.entity.MemberRole;
import com.hunmin.domain.jwt.JWTUtil;
import com.hunmin.domain.service.MemberService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// 회원 가입, 회원 정보 수정 컨트롤러 구현
@RestController
@RequestMapping("/api/members")
@Log4j2
public class MemberController {

    private final MemberService memberService;
    private final JWTUtil jwtUtil;

    public MemberController(MemberService memberService, JWTUtil jwtUtil) {
        this.memberService = memberService;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping("/uploads")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile image) {
        try {
            String imageUrl = memberService.uploadImage(image);
            return ResponseEntity.ok(imageUrl);  // 이미지 URL 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 업로드 실패");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerProcess(@RequestBody MemberDTO memberDTO) {
        try {
            memberService.registerProcess(memberDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("회원 가입 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 가입 실패");
        }
    }

    @PutMapping("/{memberId}")
    public ResponseEntity<?> updateMember(@PathVariable Long memberId, @RequestBody MemberDTO memberDTO) {
        memberService.updateMember(memberId, memberDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {

        log.info("=== MemberController - 토큰 재발급 메서드 호출");
        log.info("=== Request URI: {}", request.getRequestURI());
        log.info("=== Request Method: {}", request.getMethod());

        // refresh token을 쿠키에서 꺼냄
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                    break;
                }
            }
        }

        if (refresh == null) {
            // response token이 없으면 상태 코드 반환
            return new ResponseEntity<>("refresh token이 없습니다.", HttpStatus.BAD_REQUEST);
        }

        // refresh token 만료 확인
        try {
            // 만료되었다면 예외 발생
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            // 만료 시 상태 코드 반환
            return new ResponseEntity<>("refresh token이 만료되었습니다.", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh token인지 category 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            // refresh token이 아니면 상태 코드 반환
            return new ResponseEntity<>("잘못된 refresh token 입니다.", HttpStatus.BAD_REQUEST);
        }

        // 토큰에서 유저 정보 get
        String email = jwtUtil.getEmail(refresh);
        String role = jwtUtil.getRole(refresh).replace("ROLE_", "");

        // 새로운 access token 발급
        String newAccess = jwtUtil.createJwt("access", email, MemberRole.valueOf(role), 600000L); // 10분

        // 상태 정보 반환
        response.setHeader("access", newAccess);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}