package com.hunmin.domain.member.dto;

import lombok.Getter;

@Getter
public class MemberImageResponse {
    private final Long memberId;
    private final String profileImageUrl;
    private final int width;
    private final int height;

    public MemberImageResponse(Long memberId, String profileImageUrl, int width, int height) {
        this.memberId = memberId;
        this.profileImageUrl = profileImageUrl;
        this.width = width;
        this.height = height;
    }
}
