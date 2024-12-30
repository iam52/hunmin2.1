package com.hunmin.domain.chatmessage.dto;

import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequestDTO{
    private Long chatRoomId;
    private Long MemberId;
    private String nickName;
    private LocalDateTime createdAt;
    private Long userCount;
}
