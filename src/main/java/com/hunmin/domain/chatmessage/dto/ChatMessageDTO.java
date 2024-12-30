package com.hunmin.domain.chatmessage.dto;

import com.hunmin.domain.chatmessage.entity.ChatMessage;
import com.hunmin.domain.chatroom.entity.ChatRoom;
import com.hunmin.domain.member.entity.Member;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class ChatMessageDTO {
    private Long chatMessageId;
    private Long chatRoomId;
    private Long memberId;
    private String nickName;
    private String message;
    private MessageType type;
    private LocalDateTime createdAt;

    public ChatMessageDTO(ChatMessage chatMessage) {
        this.chatMessageId = chatMessage.getChatMessageId();
        this.chatRoomId = chatMessage.getChatRoom().getChatRoomId();
        this.memberId = chatMessage.getMember().getMemberId();
        this.message = chatMessage.getMessage();
        this.type = chatMessage.getType();
        this.createdAt = chatMessage.getCreatedAt();
        this.nickName = chatMessage.getMember().getNickname();
    }
    public ChatMessage toEntity(){
       ChatRoom chatRoom = ChatRoom.builder().chatRoomId(chatRoomId).build();
       Member member= Member.builder().memberId(memberId).build();

       return ChatMessage.builder()
                .chatMessageId(chatMessageId)
                .chatRoom(chatRoom)
                .member(member)
                .message(message)
                .type(type)
                .build();
    }
}
