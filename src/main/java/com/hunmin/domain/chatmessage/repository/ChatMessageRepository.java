package com.hunmin.domain.chatmessage.repository;

import com.hunmin.domain.chatmessage.entity.ChatMessage;
import com.hunmin.domain.chatmessage.repository.search.ChatMessageSearch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>, ChatMessageSearch {
}
