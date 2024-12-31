package com.hunmin.domain.member.dto;

import com.hunmin.domain.member.entity.MemberLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private String token;
    private String refreshToken;
    private Long memberId;
    private String role;
    private String nickname;
    private String image;
    private String email;
    private MemberLevel level;
    private String country;
}
