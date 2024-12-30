package com.hunmin.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hunmin.domain.dto.chat.ChatRoomDTO;
import com.hunmin.domain.dto.chat.ChatRoomRequestDTO;
import com.hunmin.domain.entity.ChatRoom;
import com.hunmin.domain.entity.Member;
import com.hunmin.domain.handler.SseEmitters;
import com.hunmin.domain.repository.ChatRoomRepository;
import com.hunmin.domain.repository.MemberRepository;
import com.hunmin.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

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
        ChatRoom chatRoom = chatRoomRepository.findById(id).orElseThrow(ErrorCode.CHAT_ROOM_NOT_FOUND::throwException);
        return new ChatRoomDTO(chatRoom);
    }

    // 관련 채팅방 조회
    public List<ChatRoomRequestDTO> findRoomByEmail(String email) {
        Member me = memberRepository.findByEmail(email).orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);

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
        Member partner = memberRepository.findByNickname(partnerName).orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);
        Member me = memberRepository.findByEmail(myEmail).orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);
        validateNoDuplicateRoom(me.getNickname(), partnerName);

        ChatRoom chatRoom = ChatRoom.builder().member(me).build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        ChatRoomRequestDTO chatRoomRequestDTO = convertToDTO(savedChatRoom, me, partnerName);
        roomStorage.put(me.getNickname(), partnerName, chatRoomRequestDTO);
        return chatRoomRequestDTO;
    }

    private void validateNoDuplicateRoom(String myNickname, String partnerName) {
        if (roomStorage.get(myNickname, partnerName) != null || roomStorage.get(partnerName, myNickname) != null) {
            throw ErrorCode.CHAT_ROOM_ALREADY_EXIST.throwException();
        }
    }

    private ChatRoomRequestDTO convertToDTO(ChatRoom chatRoom, Member member, String partnerName) {
        return ChatRoomRequestDTO.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .memberId(member.getMemberId())
                .nickName(member.getNickname())
                .partnerName(partnerName)
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }

    // 채팅방 삭제
    public Boolean deleteChatRoom(Long chatRoomId, String partnerName, String meEmail) {
        Member partner = memberRepository.findByNickname(partnerName).orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);
        if (partner == null) {
            return false;
        }
        Member me = memberRepository.findByEmail(meEmail).orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(ErrorCode.CHAT_ROOM_NOT_FOUND::throwException);
        if (roomStorage.get(me.getNickname(), partnerName) != null) {
            roomStorage.delete(me.getNickname(), partnerName);
        } else if (roomStorage.get(partnerName, me.getNickname()) != null) {
            roomStorage.delete(partnerName, me.getNickname());
        } else return false;
        chatRoomRepository.delete(chatRoom);
        return true;
    }
}
