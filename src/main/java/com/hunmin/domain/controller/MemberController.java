package com.hunmin.domain.controller;

import com.hunmin.domain.dto.member.MemberRequest;
import com.hunmin.domain.dto.member.MemberResponse;
import com.hunmin.domain.dto.member.PasswordFindRequestDto;
import com.hunmin.domain.dto.member.PasswordUpdateRequestDto;
import com.hunmin.domain.entity.MemberRole;
import com.hunmin.domain.entity.RefreshEntity;
import com.hunmin.domain.jwt.JWTUtil;
import com.hunmin.domain.repository.RefreshRepository;
import com.hunmin.domain.service.MemberService;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@RestController
@RequestMapping("/api/members")
@Slf4j
@Tag(name = "회원", description = "회원 CRUD")
public class MemberController {

    private final MemberService memberService;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    // 생성자 주입
    public MemberController(MemberService memberService, JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.memberService = memberService;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @PostMapping
    @Operation(summary = "회원 가입", description = "회원 가입할 때 사용하는 API")
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody MemberRequest memberRequest) {
        MemberResponse response = memberService.register(memberRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{nickname}")
    @Operation(summary = "회원 검색", description = "닉네임으로 회원 검색")
    public ResponseEntity<MemberResponse> searchMember(@PathVariable String nickname) {
        MemberResponse memberResponse = memberService.getMember(nickname);
        return ResponseEntity.ok(memberResponse);
    }

    @PutMapping("/{memberId}")
    @Operation(summary = "회원 정보 수정", description = "등록된 회원의 정보를 수정할 때 사용하는 API")
    public ResponseEntity<?> updateMember(@PathVariable Long memberId, @RequestBody MemberRequest memberRequest) {
        memberService.updateMember(memberId, memberRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/uploads")
    @Operation(summary = "프로필 사진 등록", description = "회원 가입 시 프로필 사진을 등록할 때 사용하는 API")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile image) {
        try {
            String imageUrl = memberService.uploadImage(image);
            return ResponseEntity.ok(imageUrl);  // 이미지 URL 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 업로드 실패");
        }
    }

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급", description = "refresh token으로 access token 재발급하는 API")
    public ResponseEntity<?> reissueToken(HttpServletRequest request, HttpServletResponse response) {

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

        // DB에 저장되어 있는지 확인
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {
            return new ResponseEntity<>("Refresh Token이 유효하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // 토큰에서 유저 정보 get
        String email = jwtUtil.getEmail(refresh);
        String role = jwtUtil.getRole(refresh).replace("ROLE_", "");

        // 새로운 access & refresh token 발급
        String newAccess = jwtUtil.createJwt("access", email, MemberRole.valueOf(role), 6000000L); // 100분
        String newRefresh = jwtUtil.createJwt("refresh", email, MemberRole.valueOf(role), 86400000L); // 24시간

        // Refresh Token 저장 DB에 기존 Refresh Token 삭제 후 새 Refresh Token 저장
        refreshRepository.deleteByRefresh(refresh);
        addRefreshEntity(email, newRefresh, 86400000L);

        // 상태 정보 반환
        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 쿠키 생성 메서드
    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);
        return cookie;
    }

    // Refresh Token 저장 메서드
    private void addRefreshEntity(String email, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);
        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setEmail(email);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());
        refreshRepository.save(refreshEntity);
    }

    @PostMapping("/password/verify")
    public ResponseEntity<?> verifyUser(@RequestBody PasswordFindRequestDto passwordFindRequestDto) {
        return memberService.verifyUserForPasswordReset(passwordFindRequestDto);
    }

    @PostMapping("/password/update")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateRequestDto passwordUpdateRequestDto) {
        return memberService.updatePassword(passwordUpdateRequestDto);
    }
}
