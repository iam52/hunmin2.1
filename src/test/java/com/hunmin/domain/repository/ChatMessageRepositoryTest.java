package com.hunmin.domain.repository;

import com.hunmin.domain.chatmessage.dto.ChatMessageDTO;
import com.hunmin.domain.chatmessage.dto.ChatMessageListRequestDTO;
import com.hunmin.domain.chatmessage.dto.MessageType;
import com.hunmin.domain.chatmessage.entity.ChatMessage;
import com.hunmin.domain.chatmessage.repository.ChatMessageRepository;
import com.hunmin.domain.chatroom.entity.ChatRoom;
import com.hunmin.domain.member.entity.Member;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@Log4j2
public class ChatMessageRepositoryTest {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Test
    public void createChatMessage() {
        //given
        Long chatRoomId = 1L;
        Long memberId = 1L;
        String message = "test message";

        ChatRoom chatRoom = ChatRoom.builder().chatRoomId(chatRoomId).build();
        Member member = Member.builder().memberId(memberId).build();

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(member)
                .message(message)
                .type(MessageType.TALK)
                .build();
        //when
        ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO(savedChatMessage);

        //then
        assertNotNull(chatMessageDTO);
        assertEquals(chatMessageDTO.getChatRoomId(), chatRoomId);
        assertEquals(chatMessageDTO.getMemberId(), memberId);
        assertEquals(chatMessageDTO.getMessage(), message);

        log.info("chatMessageDTO= {}",chatMessageDTO);
    }
    @Test
    public void readChatMessages() {
        //given
        Long chatRoomId = 1L;
        Long memberId = 1L;
        String message = "test message";

        ChatRoom chatRoom = ChatRoom.builder().chatRoomId(chatRoomId).build();
        Member member = Member.builder().memberId(memberId).build();

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(member)
                .message(message)
                .type(MessageType.TALK)
                .build();
        //when
        ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO(savedChatMessage);
        Optional<ChatMessage> foundChatMessage = chatMessageRepository.findById(chatMessageDTO.getChatMessageId());
        chatMessageDTO = new ChatMessageDTO(chatMessage);

        //then
        assertThat(chatMessageDTO).isNotNull();
        assertThat(chatMessageDTO.getChatRoomId()).isEqualTo(chatRoomId);
        assertThat(chatMessageDTO.getMessage()).isEqualTo(message);
    }


    @Test
    public void deleteChatMessage() {
        //given
        Long chatRoomId = 1L;
        Long memberId = 1L;
        String message = "test message";

        ChatRoom chatRoom = ChatRoom.builder().chatRoomId(chatRoomId).build();
        Member member = Member.builder().memberId(memberId).build();

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(member)
                .message(message)
                .type(MessageType.TALK)
                .build();

        //when
        ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);
        log.info("savedChatMessage={}",savedChatMessage);
        assertNotNull(savedChatMessage);
        chatMessageRepository.delete(savedChatMessage);
        Optional<ChatMessage> foundChatMessage = chatMessageRepository.findById(savedChatMessage.getChatMessageId());

        //then
        assertThat(foundChatMessage).isEmpty();
    }
    @Test
    public void updateChatMessage() {
        //given
        Long chatRoomId = 1L;
        Long memberId = 1L;
        String message = "test message";

        ChatRoom chatRoom = ChatRoom.builder().chatRoomId(chatRoomId).build();
        Member member = Member.builder().memberId(memberId).build();

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(member)
                .message(message)
                .type(MessageType.TALK)
                .build();

        //when
        ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO(savedChatMessage);
        chatMessage = chatMessageDTO.toEntity();
        log.info("chatMessageEntity={}",chatMessage.toString());
        chatMessage.setMessage("test updated message");
        chatMessage.setType(MessageType.QUIT);
        savedChatMessage = chatMessageRepository.save(chatMessage);
        log.info("updatedChatMessage={}",savedChatMessage.toString());
        ChatMessage entity = new ChatMessageDTO(savedChatMessage).toEntity();
        log.info("chatMessageDTO second ={}",entity.toString());

        //then
        assertThat(savedChatMessage).isNotNull();
        assertThat(savedChatMessage.getMessage()).isEqualTo("test updated message");
        assertThat(savedChatMessage.getType()).isEqualTo(MessageType.QUIT);
    }
    @Test
    public void readChatMessageList(){
        //given
        IntStream.rangeClosed(1, 100).forEach(i->{
            Long chatRoomId = 1L;
            Long memberId = 1L;
            String message = "test message";

            ChatRoom chatRoom = ChatRoom.builder().chatRoomId(chatRoomId).build();
            Member member = Member.builder().memberId(memberId).build();

            ChatMessage chatMessage = ChatMessage.builder()
                    .chatRoom(chatRoom)
                    .member(member)
                    .message(message+i)
                    .type(MessageType.TALK)
                    .build();
            chatMessageRepository.save(chatMessage);
        });
        //when
        Long chatRoomId = 1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<ChatMessageListRequestDTO> chatMessageListRequestDTOS  = chatMessageRepository.chatMessageList (pageable, chatRoomId);

        //then
        assertEquals(408, chatMessageListRequestDTOS.getTotalElements()); //전체 게시물 수
        assertEquals(41, chatMessageListRequestDTOS.getTotalPages());     //총 페이지 수
        assertEquals(0,  chatMessageListRequestDTOS.getNumber()) ;        //현재 페이지 번호 0
        assertEquals(10, chatMessageListRequestDTOS.getSize());           //한 페이지 게시물 수 10
        assertEquals(10, chatMessageListRequestDTOS.getContent().size()); //      "

        chatMessageListRequestDTOS.getContent().forEach(System.out::println);
    }
}
