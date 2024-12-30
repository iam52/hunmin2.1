package com.hunmin.domain.chatroom.controller;

import com.hunmin.domain.chatmessage.dto.ChatRoomDTO;
import com.hunmin.domain.chatmessage.dto.ChatRoomRequestDTO;
import com.hunmin.domain.chatroom.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequiredArgsConstructor
@RestController
@Log4j2
@RequestMapping("/api/chat-room")
@Tag(name = "채팅목록", description = "채팅목록 CRUD")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    //채팅방 생성
    @PostMapping("/{nickName}")
    @Operation(summary = "채팅방 생성", description = "채팅방을 이름으로 생성하는 API")
    public ResponseEntity<ChatRoomRequestDTO> createRoomByNickName(@PathVariable("nickName") String nickName
                                                        , Authentication authentication) {
        String currentMemberEmail = authentication.getName();
        return ResponseEntity.ok(chatRoomService.createChatRoomByNickName(nickName, currentMemberEmail));
    }

    //나랑 관련된 채팅방만 조회
    @GetMapping("/list")
    @Operation(summary = "채팅방 조회", description = "사용자와 관련된 채팅방 조회하는 API")
    public List<ChatRoomRequestDTO> myRooms(Authentication authentication) {
        String currentMemberEmail = authentication.getName();
        return chatRoomService.findRoomByEmail(currentMemberEmail);
    }

    //채팅방 안으로 입장
    @GetMapping("/enter/{chatRoomId}")
    @Operation(summary = "채팅방 입장", description = "채팅방으로 입장하는 API")
    public String RoomEnter(@PathVariable String chatRoomId) {
        return chatRoomId;
    }
    //단일 채팅방 정보 조회
    @GetMapping("/{chatRoomId}")
    @Operation(summary = "단일 채팅방 정보 조회", description = "검색하고 싶은 채팅방을 조회하는 API")
    public ChatRoomDTO roomInfo(@PathVariable Long chatRoomId) {
        return chatRoomService.findRoomById(chatRoomId);
    }
    //채팅방 삭제
    @DeleteMapping("/{chatRoomId}/{partnerName}")
    @Operation(summary = "채팅방 삭제", description = "삭제하고 싶은 채팅방을 삭제하는 API")
    public Boolean deleteRoom(@PathVariable Long chatRoomId, @PathVariable String partnerName,Authentication authentication) {
        return chatRoomService.deleteChatRoom(chatRoomId,partnerName,authentication.getName());
    }
}
