package com.hunmin.domain.follow.dto;

import com.hunmin.domain.follow.entity.Follow;
import com.hunmin.domain.follow.entity.FollowStatus;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class FollowRequestDTO {
    private Long followId;
    private Long followerId;
    private Long followeeId;
    private Boolean isBlock;
    private Boolean notification;
    private LocalDateTime createdAt;
    private FollowStatus status;
    //팔로워
    private String followerName;
    private String followerEmail;
    private String followerImage;
    //팔로이
    private String followeeEmail;
    private String followeeImage;
    private String followeeName;

    public FollowRequestDTO(Follow follow) {
        this.followId = follow.getFollowId();
        this.followerId = follow.getFollower().getMemberId();
        this.followeeId = follow.getFollowee().getMemberId();
        this.isBlock = follow.getIsBlock();
        this.notification = follow.getNotification();
        this.createdAt = follow.getCreatedAt();
        this.status = follow.getStatus();
        //팔로워
        this.followerName = follow.getFollower().getNickname();
        this.followerEmail = follow.getFollower().getEmail();
        this.followerImage = follow.getFollower().getImage();
        //팔로이
        this.followeeName = follow.getFollowee().getNickname();
        this.followeeEmail = follow.getFollowee().getEmail();
        this.followeeImage = follow.getFollowee().getImage();
    }
}
