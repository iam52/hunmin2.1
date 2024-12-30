package com.hunmin.domain.chatmessage.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomRequestDTO {
    private Long chatRoomId;
    private Long memberId;
    private String nickName;
    private String partnerName;
    private LocalDateTime createdAt;
}
