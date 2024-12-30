package com.hunmin.domain.chatmessage.repository.search;

import com.hunmin.domain.chatmessage.dto.ChatMessageListRequestDTO;
import com.hunmin.domain.chatmessage.entity.ChatMessage;
import com.hunmin.domain.chatmessage.entity.QChatMessage;
import com.hunmin.domain.chatroom.entity.QChatRoom;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class ChatMessageSearchImpl extends QuerydslRepositorySupport implements ChatMessageSearch {
    public ChatMessageSearchImpl() {
        super(ChatMessage.class);
    }

    @Override
    public Page<ChatMessageListRequestDTO> chatMessageList(Pageable pageable, Long chatRoomId) {
        QChatMessage chatMessage = QChatMessage.chatMessage;
        QChatRoom chatRoom = QChatRoom.chatRoom;

        JPQLQuery<ChatMessage> query
                = from(chatMessage).leftJoin(chatMessage.chatRoom, chatRoom)
                .where(chatRoom.chatRoomId.eq(chatRoomId));

        JPQLQuery<ChatMessageListRequestDTO> dtoQuery = query.select(Projections.bean(
                ChatMessageListRequestDTO.class
                ,chatMessage.message
                ,chatMessage.chatMessageId.as("chatMessageId")
                ,chatMessage.member.memberId.as("memberId")
                ,chatMessage.createdAt,
                chatMessage.type));

        getQuerydsl().applyPagination(pageable, dtoQuery);
        List<ChatMessageListRequestDTO> chatMessageList = dtoQuery.fetch();
        long count = dtoQuery.fetchCount();

        return new PageImpl<>(chatMessageList, pageable, count);
    }
}
