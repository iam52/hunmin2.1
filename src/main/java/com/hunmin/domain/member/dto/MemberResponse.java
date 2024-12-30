package com.hunmin.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hunmin.domain.member.entity.Member;
import com.hunmin.domain.member.entity.MemberLevel;
import com.hunmin.domain.member.entity.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MemberResponse {
    private Long memberId;
    private String email;
    private String nickname;
    private MemberLevel level;
    private String country;
    private MemberRole memberRole;
    private String image;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    // Entity -> Response 변환 메서드
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getMemberId(),
                member.getEmail(),
                member.getNickname(),
                member.getLevel(),
                member.getCountry(),
                member.getMemberRole(),
                member.getImage(),
                member.getCreatedAt()
        );
    }
}