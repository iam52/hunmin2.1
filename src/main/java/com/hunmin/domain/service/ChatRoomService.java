package com.hunmin.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hunmin.domain.dto.chat.ChatRoomDTO;
import com.hunmin.domain.dto.chat.ChatRoomRequestDTO;
import com.hunmin.domain.dto.notification.NotificationSendDTO;
import com.hunmin.domain.entity.ChatRoom;
import com.hunmin.domain.entity.Member;
import com.hunmin.domain.entity.NotificationType;
import com.hunmin.domain.handler.SseEmitters;
import com.hunmin.domain.repository.ChatRoomRepository;
import com.hunmin.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class ChatRoomService {

    private final HashOperations<String, String, Object> roomStorage;
    private final ObjectMapper objectMapper;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;
    private final SseEmitters sseEmitters;

    // 단일 채팅방 조회
    public ChatRoomDTO findRoomById(Long id) {
        ChatRoom chatRoom = chatRoomRepository.findById(id).orElseThrow(ChatRoomException.NOT_FOUND::get);
        return new ChatRoomDTO(chatRoom);
    }

    //관련 채팅방 조회
    public List<ChatRoomRequestDTO> findRoomByEmail(String email) {
        Member me = memberRepository.findByEmail(email);

        List<Object> partnerNameAndChatRoom = roomStorage.values(me.getNickname());

        Set<Long> chatRoomIds = new HashSet<>();
        List<ChatRoomRequestDTO> chatRoomRequestDTOList = new ArrayList<>();

        for (Object chatRoomRequestDTO : partnerNameAndChatRoom) {
            ChatRoomRequestDTO chatRoomRequest = objectMapper.convertValue(chatRoomRequestDTO, ChatRoomRequestDTO.class);
            chatRoomIds.add(chatRoomRequest.getChatRoomId());
            chatRoomRequestDTOList.add(chatRoomRequest);
        }

        List<Member> allMembers = memberRepository.findAll();
        for (Member memberIndex : allMembers) {
            Object rawChatRoom = roomStorage.get(memberIndex.getNickname(), me.getNickname());
            ChatRoomRequestDTO chatRoomRequestDTO = objectMapper.convertValue(rawChatRoom, ChatRoomRequestDTO.class);
            if (chatRoomRequestDTO != null) {
                if (!chatRoomIds.contains(chatRoomRequestDTO.getChatRoomId())) {
                    chatRoomIds.add(chatRoomRequestDTO.getChatRoomId());
                    chatRoomRequestDTOList.add(chatRoomRequestDTO);
                }
            }
        }
        return chatRoomRequestDTOList;
    }


    // 채팅방 생성
    public ChatRoomRequestDTO createChatRoomByNickName(String partnerName, String myEmail) {

        Optional<Member> byNickname = memberRepository.findByNickname(partnerName);
        if (byNickname.isEmpty()) {
            throw MemberException.NOT_FOUND.get();
        }

        Member me = memberRepository.findByEmail(myEmail);

        String myNickname = me.getNickname();

        if (roomStorage.get(myNickname, partnerName) != null || roomStorage.get(partnerName, myNickname) != null) {
            throw ChatRoomException.CHATROOM_ALREADY_EXIST.get();
        }
        ChatRoom chatRoom = ChatRoom.builder().member(me).build();
        ChatRoom SavedchatRoom = chatRoomRepository.save(chatRoom);
        ChatRoomRequestDTO chatRoomRequestDTO = ChatRoomRequestDTO.builder()
                .chatRoomId(SavedchatRoom.getChatRoomId())
                .memberId(me.getMemberId())
                .nickName(me.getNickname())
                .partnerName(partnerName)
                .createdAt(SavedchatRoom.getCreatedAt())
                .build();
        roomStorage.put(me.getNickname(), partnerName, chatRoomRequestDTO);

        Member partner = memberRepository.findByNickname(partnerName).orElseThrow(MemberException.NOT_FOUND::get);

        if (partner != null) {
            Long partnerId = partner.getMemberId();

            NotificationSendDTO notificationSendDTO = NotificationSendDTO.builder()
                    .memberId(partnerId)
                    .message("[" + me.getNickname() + "]님이 새로운 채팅방을 개설")
                    .notificationType(NotificationType.CHAT)
                    .url("/chat-room/" + SavedchatRoom.getChatRoomId())
                    .build();

            notificationService.send(notificationSendDTO);

            String emitterId = partnerId + "_";
            SseEmitter emitter = sseEmitters.findSingleEmitter(emitterId);

            if (emitter != null) {
                try {
                    emitter.send(new ChatRoomDTO(SavedchatRoom));
                } catch (IOException e) {
                    log.error("Error sending chat room notification to client via SSE: {}", e.getMessage());
                    sseEmitters.delete(emitterId);
                }
            }
        }
        return ChatRoomRequestDTO.builder()
                .chatRoomId(SavedchatRoom.getChatRoomId())
                .partnerName(partnerName)
                .nickName(me.getNickname())
                .memberId(me.getMemberId())
                .createdAt(SavedchatRoom.getCreatedAt())
                .build();
    }

    // 채팅방 삭제
    public Boolean deleteChatRoom(Long chatRoomId, String partnerName, String meEmail) {

        Optional<Member> partner = memberRepository.findByNickname(partnerName);
        if (partner.isEmpty()) {
            return false;
        }

        Member me = memberRepository.findByEmail(meEmail);
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(ChatRoomException.NOT_FOUND::get);

        if (roomStorage.get(me.getNickname(), partnerName) != null) {
            roomStorage.delete(me.getNickname(), partnerName);
        } else if (roomStorage.get(partnerName, me.getNickname()) != null) {
            roomStorage.delete(partnerName, me.getNickname());
        } else return false;
        chatRoomRepository.delete(chatRoom);

        return true;
    }
}
