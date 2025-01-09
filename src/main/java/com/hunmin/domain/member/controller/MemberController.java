package com.hunmin.domain.member.controller;

import com.hunmin.domain.member.dto.*;
import com.hunmin.domain.member.service.MemberService;
import com.hunmin.global.exception.ErrorCode;
import com.hunmin.global.security.dto.TokenResponse;
import com.hunmin.global.security.entity.CustomUserDetails;
import com.hunmin.global.security.jwt.CookieUtil;
import com.hunmin.global.security.service.TokenService;
import com.sun.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/members")
@Slf4j
@Tag(name = "회원", description = "회원 CRUD")
public class MemberController {

    private final MemberService memberService;
    private final TokenService tokenService;

    // 생성자 주입
    public MemberController(MemberService memberService, TokenService tokenService) {
        this.memberService = memberService;
        this.tokenService = tokenService;
    }

    @PostMapping
    @Operation(summary = "회원 가입", description = "회원 가입할 때 사용하는 API")
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody MemberRequest memberRequest) {
        MemberResponse response = memberService.register(memberRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/uploads")
    @Operation(summary = "프로필 사진 등록", description = "회원 가입 시 프로필 사진을 등록할 때 사용하는 API")
    public ResponseEntity<MemberImageResponse> uploadImage(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam("image") MultipartFile image) throws IOException {
        MemberImageResponse memberImageResponse = memberService.uploadProfileImage(customUserDetails.getMemberId(), image);
        return ResponseEntity.status(HttpStatus.CREATED).body(memberImageResponse);
    }

    @GetMapping("/{nickname}")
    @Operation(summary = "회원 검색", description = "닉네임으로 회원 검색")
    public ResponseEntity<MemberResponse> searchMember(@PathVariable String nickname) {
        MemberResponse memberResponse = memberService.getMember(nickname);
        return ResponseEntity.ok(memberResponse);
    }

    @PutMapping("/{memberId}")
    @Operation(summary = "회원 정보 수정", description = "등록된 회원의 정보를 수정할 때 사용하는 API")
    public ResponseEntity<MemberResponse> updateMember(@PathVariable Long memberId, @RequestBody MemberRequest memberRequest) {
        MemberResponse memberResponse = memberService.updateMember(memberId, memberRequest);
        return ResponseEntity.ok(memberResponse);
    }

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급", description = "refresh token으로 access token 재발급하는 API")
    public ResponseEntity<?> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        // refresh token을 쿠키에서 추출
        String refreshToken = extractRefreshToken(request).orElseThrow(ErrorCode.REFRESH_TOKEN_NOT_FOUND::throwException);
        TokenResponse tokens = tokenService.reissue(refreshToken);
        // 응답 설정
        response.setHeader("access", tokens.getAccessToken());
        response.addCookie(CookieUtil.createRefreshTokenCookie("refresh", tokens.getRefreshToken()));
        return ResponseEntity.ok().body("리프레시 토큰이 재발행 되었습니다.");
    }
    private Optional<String> extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    return Optional.ofNullable(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    @PostMapping("/password/verify")
    public ResponseEntity<?> verifyUser(@RequestBody PasswordFindRequest passwordFindRequest) {
        return memberService.verifyUserForPasswordReset(passwordFindRequest);
    }

    @PostMapping("/password/update")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateRequest passwordUpdateRequest) {
        return memberService.updatePassword(passwordUpdateRequest);
    }
}
