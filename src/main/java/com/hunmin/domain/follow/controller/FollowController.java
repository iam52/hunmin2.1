package com.hunmin.domain.follow.controller;

import com.hunmin.domain.follow.dto.FollowRequestDTO;
import com.hunmin.global.common.PageRequestDTO;
import com.hunmin.domain.follow.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api/follow")
@RestController
@RequiredArgsConstructor
@Log4j2
public class FollowController {

    private final FollowService followService;

    // 팔로이 등록 요청
    @PostMapping("/{memberId}")
    @Operation(summary = "팔로이 등록 요청", description = "팔로이 등록 요청 호출 API")
    public ResponseEntity<FollowRequestDTO> registerFollower(@PathVariable Long memberId
                                                            ,Authentication authentication){
        String myEmail = authentication.getName();
        return ResponseEntity.ok(followService.register(myEmail, memberId));
    }
    // 팔로이 수락 요청
    @GetMapping("/{memberId}")
    @Operation(summary = "팔로이 수락 요청", description = "팔로이 수락 요청 호출 API")
    public ResponseEntity<FollowRequestDTO> AcceptFollower(@PathVariable Long memberId
                                                          ,Authentication authentication){
        String myEmail = authentication.getName();
        return ResponseEntity.ok(followService.registerAccept(myEmail, memberId));
    }
    // 팔로이 삭제
    @DeleteMapping("/{memberId}")
    @Operation(summary = "팔로이 삭제", description = "팔로이 삭제 호출 API")
    public ResponseEntity<Boolean> deleteFollower(@PathVariable Long memberId
                                                  ,Authentication authentication){
        String myEmail = authentication.getName();
        return ResponseEntity.ok(followService.remove(myEmail, memberId));
    }

    // 팔로우 리스트 조회
    @GetMapping("/list")
    @Operation(summary = "팔로이 리스트 조회", description = "팔로이 리스트 호출 API")
    public ResponseEntity<Page<FollowRequestDTO>> loadMessageList(Authentication authentication,
                                                                  @RequestParam(value = "page", defaultValue = "1") int page,
                                                                  @RequestParam(value = "size", defaultValue = "10") int size) {
        String email = authentication.getName();
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().page(page).size(size).build();
        return ResponseEntity.ok(followService.readPage(pageRequestDTO, email));
    }
    // 알림 변경 요청
    @PostMapping("/notification/{memberId}")
    @Operation(summary = "알림 변경 요청", description = "알림 변경 요청 호출 API")
    public ResponseEntity<Boolean> changeNotification(@PathVariable Long memberId
                                                     ,Authentication authentication){
        String myEmail = authentication.getName();
        return ResponseEntity.ok(followService.turnNotification(myEmail, memberId));
    }
    // 차단 상태 변경 요청
    @PostMapping("/block/{memberId}")
    @Operation(summary = "차단 상태 변경 요청", description = "차단 상태 변경 요청 호출 API")
    public ResponseEntity<Boolean> blockFollower(@PathVariable Long memberId
                                                ,Authentication authentication){
        String myEmail = authentication.getName();
        return ResponseEntity.ok(followService.blockFollower(myEmail, memberId));
    }
    // follow 상태 확인 요청
    @GetMapping("/check/{targetMemberId}")
    @Operation(summary = "follow 상태 확인 요청", description = "follow 상태 확인 요청 호출 API")
    public ResponseEntity<Map<String, Boolean>> checkFollowStatus(@RequestParam("memberId") Long memberId,
                                                                  @PathVariable("targetMemberId") Long targetMemberId) {
        boolean isFollowing = followService.isFollowing(memberId, targetMemberId);
        Map<String, Boolean> response = Map.of("isFollowing", isFollowing);
        return ResponseEntity.ok(response);
    }

}
