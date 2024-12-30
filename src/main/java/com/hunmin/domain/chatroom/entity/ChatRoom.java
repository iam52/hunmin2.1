package com.hunmin.domain.chatroom.entity;

import com.hunmin.domain.chatmessage.entity.ChatMessage;
import com.hunmin.domain.member.entity.Member;
import com.hunmin.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.List;

@Entity
@Getter
@Builder
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="member_id")
    private Member member;

    @BatchSize( size = 50 )
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL,orphanRemoval = true, fetch = FetchType.EAGER) // @JoinColumn 제거
    private List<ChatMessage> chatMessage;

    @Builder.Default
    private long userCount=1;

    public void add(ChatMessage chatMessage){
        this.chatMessage.add(chatMessage);
        chatMessage.setChatRoom(this);
    }
}
