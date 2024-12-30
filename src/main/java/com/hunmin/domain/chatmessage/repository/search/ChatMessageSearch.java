package com.hunmin.domain.chatmessage.repository.search;

import com.hunmin.domain.chatmessage.dto.ChatMessageListRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatMessageSearch {
    Page<ChatMessageListRequestDTO> chatMessageList(Pageable pageable, Long chatRoomId);
}
