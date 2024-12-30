package com.hunmin.domain.chatmessage.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChatMessageListRequestDTO {
    private Long chatMessageId;
    private Long memberId;
    private String message;
    private LocalDateTime createdAt;
    private MessageType type;
}
