package com.hunmin.domain.repository;

import com.hunmin.domain.dto.chat.ChatRoomDTO;
import com.hunmin.domain.dto.chat.MessageType;
import com.hunmin.domain.entity.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Log4j2
@SpringBootTest
@Transactional
public class ChatRoomRepositoryTest {

    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void createChatRoom() {
        //given
        Member member = Member.builder()
                .nickname("testNickName")
                .password("1111")
                .image("testImage")
                .email("test@test.com")
                .country("ko")
                .memberRole(MemberRole.ADMIN)
                .build();
        ChatRoom chatRoom = ChatRoom.builder()
                .member(member)
                .userCount(1)
                .build();
        //when
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        //then
        assertThat(savedChatRoom).isNotNull();
        assertThat(savedChatRoom.getMember()).isEqualTo(member);
        assertThat(savedChatRoom.getUserCount()).isEqualTo(1);
    }
    @Test
    public void readChatRoom(){
        //given
        Member member = Member.builder()
                .nickname("testNickName")
                .password("1111")
                .image("testImage")
                .email("test@test.com")
                .country("ko")
                .memberRole(MemberRole.ADMIN)
                .build();
        ChatRoom chatRoom = ChatRoom.builder()
                .member(member)
                .userCount(1)
                .build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        //when
        ChatRoom foundChatRoom = chatRoomRepository.findById(savedChatRoom.getChatRoomId())
                .orElseThrow(ChatRoomException.NOT_FOUND::get);
        ChatRoomDTO chatRoomDTO = new ChatRoomDTO(foundChatRoom);

        //then
        assertThat(chatRoomDTO.getMemberId()).isEqualTo(member.getMemberId());
        assertThat(chatRoomDTO.getUserCount()).isEqualTo(1);
    }
    @Test
    public void updateChatRoom(){
        //given
        Member member = Member.builder()
                .nickname("testNickName")
                .password("1111")
                .image("testImage")
                .email("test@test.com")
                .country("ko")
                .memberRole(MemberRole.ADMIN)
                .build();
        ChatRoom chatRoom = ChatRoom.builder()
                .member(member)
                .userCount(1)
                .build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        ChatMessage chatMessage = ChatMessage.builder().message("testChatMessage").type(MessageType.TALK).member(member).build();
        List<ChatMessage> chatMessageList = new ArrayList<>();
        chatMessageList.add(chatMessage);

        //when
        savedChatRoom.setChatMessage(chatMessageList);
        savedChatRoom.setUserCount(3);
        ChatRoom updatedChatRoom = chatRoomRepository.save(savedChatRoom);

        //then
        assertThat(updatedChatRoom.getUserCount()).isEqualTo(3);
        assertThat(updatedChatRoom.getMember().getNickname()).isEqualTo("testNickName");
        assertThat(updatedChatRoom.getChatMessage()).isEqualTo(chatMessageList);
    }
    @Test
    public void deleteChatRoom(){
        //given
        Member member = Member.builder()
                .nickname("testNickName")
                .password("1111")
                .image("testImage")
                .email("test@test.com")
                .country("ko")
                .memberRole(MemberRole.ADMIN)
                .build();
        ChatRoom chatRoom = ChatRoom.builder()
                .member(member)
                .userCount(1)
                .build();
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);

        //when
        chatRoomRepository.delete(savedChatRoom);
        Optional<ChatRoom> foundChatRoom = chatRoomRepository.findById(savedChatRoom.getChatRoomId());

        //then
        assertTrue(foundChatRoom.isEmpty());
    }
    @Test
    public void createChatRoomByNickName() {
        //given
        Member member = Member.builder()
                .nickname("testNickName")
                .password("1111")
                .image("testImage")
                .email("test@test.com")
                .country("ko")
                .memberRole(MemberRole.ADMIN)
                .build();
        ChatRoom chatRoom = ChatRoom.builder()
                .member(member)
                .userCount(1)
                .build();

    }
    @Test
    @Commit
    @Transactional
    public void putInformation(){
        IntStream.rangeClosed(0,1000).forEach(i->{
            Member member = Member.builder()
                    .nickname("testNickName"+i)
                    .password("1111"+i)
                    .image("testImage"+i)
                    .email("test"+i+"@test.com")
                    .country("ko")
                    .level(MemberLevel.INTERMEDIATE)
                    .memberRole(MemberRole.ADMIN)
                    .build();
            memberRepository.save(member);
        });
    }
    @Test
    @Commit
    @Transactional
    public void putChatMessages(){
        IntStream.rangeClosed(0,100).forEach(i->{

        });
    }
}
