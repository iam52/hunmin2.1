package com.hunmin.domain.chatmessage.dto;

import com.hunmin.domain.chatmessage.entity.ChatMessage;
import com.hunmin.domain.chatroom.entity.ChatRoom;
import com.hunmin.domain.member.entity.Member;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ToString(exclude = "chatMessageIds")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ChatRoomDTO {
    private Long chatRoomId;
    private Long memberId; //nickname = 채팅방 이름(대화상대 닉네임)
    private String nickname;
    private long userCount;

    @Builder.Default
    private List<Long> chatMessageIds = new ArrayList<>();
    private String latestMessageContent;
    private LocalDateTime latestMessageDate;

    public ChatRoomDTO(ChatRoom chatRoom) {
        this.chatRoomId = chatRoom.getChatRoomId();
        this.memberId = chatRoom.getMember().getMemberId();
        this.userCount = chatRoom.getUserCount();
        this.nickname = chatRoom.getMember().getNickname();
        if (chatRoom.getChatMessage() != null && !chatRoom.getChatMessage().isEmpty()) {
            this.latestMessageContent = chatRoom.getChatMessage().get(chatRoom.getChatMessage().size() - 1).getMessage();
            this.latestMessageDate = chatRoom.getChatMessage().get(chatRoom.getChatMessage().size() - 1).getCreatedAt();
        }
        if (chatRoom.getChatMessage() != null) {
            chatRoom.getChatMessage().forEach(i -> chatMessageIds.add(i.getChatMessageId()));
        }
    }

    public ChatRoom toEntity(){
        Member member = Member.builder().memberId(memberId).build();
        List<ChatMessage> chatMessages = new ArrayList<>();
        for (Long chatMessageId : chatMessageIds) {
            chatMessages.add(ChatMessage.builder()
                    .chatMessageId(chatMessageId)
                    .build());
        }

        return ChatRoom.builder()
                .chatRoomId(chatRoomId)
                .member(member)
                .userCount(userCount)
                .chatMessage(chatMessages)
                .build();
    }
}
