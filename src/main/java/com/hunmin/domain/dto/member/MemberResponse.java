package com.hunmin.domain.dto.member;

import com.hunmin.domain.entity.Member;
import com.hunmin.domain.entity.MemberLevel;
import com.hunmin.domain.entity.MemberRole;
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