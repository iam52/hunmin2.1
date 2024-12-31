package com.hunmin.domain.member.dto;

import com.hunmin.domain.member.entity.Member;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberStatus {
    private Long memberId;
    private String email;
    private String nickname;
    private String image;
    private int boardCount;
    private int commentCount;

    public MemberStatus(Member member , int boardCount, int commentCount) {
        this.memberId = member.getMemberId();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.image = member.getImage();
        this.boardCount = boardCount;
        this.commentCount = commentCount;
    }
}