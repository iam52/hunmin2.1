//package com.hunmin.domain.service;
//
//import com.hunmin.domain.chatmessage.dto.ChatMessageDTO;
//import com.hunmin.domain.chatmessage.dto.ChatMessageListRequestDTO;
//import com.hunmin.domain.chatmessage.dto.MessageType;
//import com.hunmin.domain.dto.page.ChatMessagePageRequestDTO;
//import com.hunmin.domain.chatmessage.entity.ChatMessage;
//import com.hunmin.domain.chatroom.entity.ChatRoom;
//import com.hunmin.domain.member.entity.Member;
//import com.hunmin.domain.member.entity.MemberRole;
//import com.hunmin.domain.exception.ChatRoomException;
//import com.hunmin.domain.chatmessage.repository.ChatMessageRepository;
//import com.hunmin.domain.chatroom.repository.ChatRoomRepository;
//import com.hunmin.domain.member.repository.MemberRepository;
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@Transactional
//@Log4j2
//public class ChatMessageServiceTest {
//
//    @Autowired
//    private ChatRoomRepository chatRoomRepository;
//    @Autowired
//    private MemberRepository memberRepository;
//    @Autowired
//    private ChatMessageRepository chatMessageRepository;
//
//    @Test
//    public void readAllMessagesTest() {
//        //given
//        Member member = Member.builder()
//                .memberId(1L)
//                .nickname("testNickName")
//                .password("1111")
//                .level("intermediate")
//                .image("testImage")
//                .email("test@test.com")
//                .country("ko")
//                .memberRole(MemberRole.ADMIN)
//                .build();
//        memberRepository.save(member);
//        ChatRoom chatRoom = ChatRoom.builder()
//                .chatRoomId(1L)
//                .chatMessage(new ArrayList<>())
//                .member(member)
//                .userCount(1)
//                .build();
//        chatRoomRepository.save(chatRoom);
//        chatRoom.add(ChatMessage.builder().chatMessageId(1L).message("안녕하세요").build());
//        //when
//        ChatRoom foundChatRoom = chatRoomRepository.findById(chatRoom.getChatRoomId())
//                .orElseThrow(ChatRoomException.NOT_FOUND::get);
//        log.info("foundChatRoom ={}", chatRoom.toString());
//
//        if (foundChatRoom == null) {
//            // 채팅방이 없으면 빈 배열 반환
//            log.info("chatRoom이 null", Collections.emptyList());
//        }
//        List<ChatMessageDTO> chatLists = foundChatRoom.getChatMessage().stream()
//                .map(ChatMessageDTO::new)
//                .collect(Collectors.toList());
//
//        Collections.reverse(chatLists);
//        //then
//        for (ChatMessageDTO chatList : chatLists) {
//            log.info("chatList ={}", chatList.toString());
//        }
//    }
//
//    @Test
//    public void readAllChatMessagesTestInPages() {
//        //given
//        IntStream.rangeClosed(1, 100).forEach(i -> {
//            Long chatRoomId = 1L;
//            Long memberId = 1L;
//            String message = "test message";
//
//            ChatRoom chatRoom = ChatRoom.builder().chatRoomId(chatRoomId).build();
//            Member member = Member.builder().memberId(memberId).build();
//
//            ChatMessage chatMessage = ChatMessage.builder()
//                    .chatRoom(chatRoom)
//                    .member(member)
//                    .message(message + i)
//                    .type(MessageType.TALK)
//                    .build();
//            chatMessageRepository.save(chatMessage);
//        });
//        ChatMessagePageRequestDTO chatMessagePageRequestDTO = ChatMessagePageRequestDTO.builder().page(1).size(10)
//                .build();
//        Long chatRoomId = 1L;
//        //when
//        Sort sort = Sort.by("createdAt").descending();
//        Pageable pageable = chatMessagePageRequestDTO.getPageable(sort);
//        Page<ChatMessageListRequestDTO> chatMessageListRequestDTOS
//                = chatMessageRepository.chatMessageList(pageable, chatRoomId);
//        //then
//        assertThat(chatMessageListRequestDTOS.getNumber()).isEqualTo(0);
//        assertThat(chatMessageListRequestDTOS.getSize()).isEqualTo(10);
//        for (ChatMessageListRequestDTO chatMessageListRequestDTO : chatMessageListRequestDTOS) {
//            log.info("chatMessageListRequestDTO ={}", chatMessageListRequestDTO.toString());
//        }
//    }
//
//
//}
