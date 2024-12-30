package com.hunmin.domain.chatmessage.service;

import com.hunmin.domain.chatmessage.dto.ChatMessageDTO;
import com.hunmin.domain.chatmessage.dto.ChatMessageListRequestDTO;
import com.hunmin.domain.notification.dto.NotificationSendDTO;
import com.hunmin.global.common.PageRequestDTO;
import com.hunmin.domain.chatmessage.entity.ChatMessage;
import com.hunmin.domain.chatroom.entity.ChatRoom;
import com.hunmin.domain.member.entity.Member;
import com.hunmin.domain.notification.entity.NotificationType;
import com.hunmin.domain.notification.service.NotificationService;
import com.hunmin.global.handler.SseEmitters;
import com.hunmin.global.pubsub.RedisSubscriber;
import com.hunmin.domain.chatmessage.repository.ChatMessageRepository;
import com.hunmin.domain.chatroom.repository.ChatRoomRepository;
import com.hunmin.domain.member.repository.MemberRepository;
import com.hunmin.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ChatMessageService {

    private final MemberRepository memberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RedisSubscriber redisSubscriber;
    private final NotificationService notificationService;
    private final SseEmitters sseEmitters;

    // 채팅방에 메시지 발송
    public void sendChatMessage(ChatMessageDTO chatMessageDTO) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatMessageDTO.getChatRoomId())
                .orElseThrow(ErrorCode.CHAT_ROOM_NOT_FOUND::throwException);
        Member sender = memberRepository.findById(chatMessageDTO.getMemberId())
                .orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(sender)
                .message(chatMessageDTO.getMessage())
                .type(chatMessageDTO.getType())
                .build();
        ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);
        redisSubscriber.sendMessage(new ChatMessageDTO(savedChatMessage));

        // 알림
        Long senderId = sender.getMemberId();
        Long receiverId = null;

        List<ChatMessage> messages = chatRoom.getChatMessage();

        for (ChatMessage message : messages) {
            if (!message.getMember().getMemberId().equals(senderId)) {
                receiverId = message.getMember().getMemberId();
                break;
            }
        }

        if (!receiverId.equals(senderId)) {
            NotificationSendDTO notificationSendDTO = NotificationSendDTO.builder()
                    .memberId(receiverId)
                    .message(sender.getNickname() + "님 : " + chatMessageDTO.getMessage())
                    .notificationType(NotificationType.CHAT)
                    .url("/chat-room/" + chatMessageDTO.getChatRoomId())
                    .build();

            notificationService.send(notificationSendDTO);

            String emitterId = receiverId + "_";
            SseEmitter emitter = sseEmitters.findSingleEmitter(emitterId);

            if (emitter != null) {
                try {
                    emitter.send(new ChatMessageDTO(chatMessage));
                } catch (IOException e) {
                    log.error("Error sending comment to client via SSE: {}", e.getMessage());
                    sseEmitters.delete(emitterId);
                }
            }
        }
    }

    // 모든 채팅기록 조회
    public List<ChatMessageDTO> readAllMessages(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(ErrorCode.CHAT_ROOM_NOT_FOUND::throwException);

        if (chatRoom == null) {
            return Collections.emptyList();
        }
        List<ChatMessageDTO> chatLists = chatRoom.getChatMessage().stream()
                .map(ChatMessageDTO::new)
                .collect(Collectors.toList());
        return chatLists;
    }

    // 채팅 조회
    public ChatMessageDTO readChatMessage(Long chatMessageId) {
        return new ChatMessageDTO(chatMessageRepository.findById(chatMessageId)
                .orElseThrow(ErrorCode.CHAT_ROOM_NOT_FOUND::throwException));
    }

    // 채팅 수정
    public ChatMessageDTO updateChatMessage(ChatMessageDTO chatMessageDTO) {
        ChatMessage foundChatMessage = chatMessageRepository.findById(chatMessageDTO.getChatMessageId())
                .orElseThrow(ErrorCode.CHAT_MESSAGE_NOT_FOUND::throwException);

        foundChatMessage.setMessage(chatMessageDTO.getMessage());
        return new ChatMessageDTO(chatMessageRepository.save(foundChatMessage));
    }

    // 채팅 삭제
    public Boolean deleteChatMessage(Long chatMessageId) {
        ChatMessage chatMessage = chatMessageRepository.findById(chatMessageId)
                .orElseThrow(ErrorCode.CHAT_MESSAGE_NOT_FOUND::throwException);
        chatMessageRepository.deleteById(chatMessageId);
        return true;
    }

    // 채팅목록 페이징
    public Page<ChatMessageListRequestDTO> getList(PageRequestDTO pageRequestDTO, Long chatRoomId) { //목록
        try {
            Sort sort = Sort.by("createdAt").descending();
            Pageable pageable = pageRequestDTO.getPageable(sort);
            return chatMessageRepository.chatMessageList(pageable, chatRoomId);
        } catch (Exception e) {
            log.error("쳇서비스 페이징 실패 ={}", e.getMessage());
            throw ErrorCode.CHAT_MESSAGE_CREATE_FAIL.throwException();
        }
    }
}