package com.hunmin.global.security.jwt;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    public static Cookie createRefreshTokenCookie(String key, String value) {
        // key와 jwt를 매개로 받아 cookie 생성
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60); // 쿠키 생명 주기
        // cookie.setSecure(true); // https 사용 시 적용
        // cookie.setPath("/"); // 쿠키 적용 범위
        cookie.setHttpOnly(true); // 자바스크립트로 쿠키에 접근 제한
        return cookie;
    }
}
