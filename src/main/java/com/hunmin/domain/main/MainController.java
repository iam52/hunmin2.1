package com.hunmin.domain.main;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

// 로그인한 계정의 이름과 계정 역할을 확인하는 컨트롤러
@RestController
public class MainController {

    @GetMapping("/main")
    public ResponseEntity<Map<String, String>> mainP() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        Map<String, String> response = new HashMap<>();
        response.put("name", name);
        response.put("role", role);

        return ResponseEntity.ok(response);
    }

}
