//package com.hunmin.domain.service;
//
//import com.hunmin.domain.chatmessage.dto.ChatMessageRequestDTO;
//import com.hunmin.domain.chatmessage.dto.ChatRoomDTO;
//import com.hunmin.domain.chatroom.entity.ChatRoom;
//import com.hunmin.domain.member.entity.Member;
//import com.hunmin.domain.member.entity.MemberRole;
//import com.hunmin.domain.exception.ChatRoomException;
//import com.hunmin.domain.chatroom.repository.ChatRoomRepository;
//import com.hunmin.domain.member.repository.MemberRepository;
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@Log4j2
//@Transactional
//public class ChatRoomServiceTest {
//
//    @Autowired
//    private ChatRoomService chatRoomService;
//    @Autowired
//    private MemberRepository memberRepository;
//    @Autowired
//    private ChatRoomRepository chatRoomRepository;
//
//    @Test
//    public void createChatRoom() {
//        //given
//        Member member = Member.builder()
//                .memberId(1L)
//                .nickname("testNickName2")
//                .password("1111")
//                .level("intermediate")
//                .image("testImage")
//                .email("test@test.com")
//                .country("ko")
//                .memberRole(MemberRole.ADMIN)
//                .build();
//        memberRepository.save(member);
//        //when
//        Member foundMember = memberRepository.findByNickname(member.getNickname())
//                .orElseThrow(() -> new RuntimeException("없는 사용자를 불러왔습니다."));
//        ChatRoom chatRoom = ChatRoom.builder().member(foundMember).userCount(1).build();
//        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
//
//        ChatMessageRequestDTO chatMessageRequestDTO = ChatMessageRequestDTO.builder()
//                .chatRoomId(savedChatRoom.getChatRoomId())
//                .userCount(savedChatRoom.getUserCount())
//                .createdAt(savedChatRoom.getCreatedAt())
//                .nickName(member.getNickname())
//                .MemberId(member.getMemberId()).build();
//        //then
//        assertThat(chatMessageRequestDTO).isNotNull();
//        assertThat(chatMessageRequestDTO.getNickName()).isEqualTo("testNickName2");
//    }
//    @Test
//    public void getUserCount(){
//        //given
//        Member member = Member.builder()
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
//                .member(member)
//                .userCount(1)
//                .build();
//        chatRoomRepository.save(chatRoom);
//        //when
//        ChatRoom foundChatRoom = chatRoomRepository.findById(chatRoom.getChatRoomId())
//                .orElseThrow(ChatRoomException.NOT_FOUND::get);
//        //then
//        assertThat(foundChatRoom).isNotNull();
//        assertThat(foundChatRoom.getUserCount()).isEqualTo(1);
//    }
//
//    @Test
//    public void findAllRoomTest() {
//        //give,when
//        List<ChatRoomDTO> chatRoomDTOs = chatRoomService.findAllRoom();
//        //then
//        assertThat(chatRoomDTOs).isNotNull();
//        assertThat(chatRoomDTOs.size()).isGreaterThan(0);
//
//        log.info(chatRoomDTOs);
//    }
//}
