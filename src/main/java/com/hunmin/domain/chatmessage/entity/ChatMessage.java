package com.hunmin.domain.chatmessage.entity;

import com.hunmin.domain.chatmessage.dto.MessageType;
import com.hunmin.domain.chatroom.entity.ChatRoom;
import com.hunmin.domain.member.entity.Member;
import com.hunmin.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatMessageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="member_id")
    private Member member;

    @Column(name = "message", nullable = false, length = 255)
    private String message;

    @Enumerated(EnumType.STRING)
    private MessageType type;
}