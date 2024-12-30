package com.hunmin.domain.admin.controller;

import com.hunmin.domain.admin.service.AdminService;
import com.hunmin.domain.board.dto.BoardResponseDTO;
import com.hunmin.domain.comment.dto.CommentResponseDTO;
import com.hunmin.domain.member.dto.MemberStatusDTO;
import com.hunmin.global.common.PageRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// ADMIN 컨트롤러 구현

@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "관리자", description = "관리자 기능")
@RestController
public class AdminController {

    private final AdminService adminService;

    @GetMapping("")
    public String adminP() {
        return "ADMIN USER CONTROLLER";
    }

    // 회원 ID로 회원 정보 검색
    @Operation(summary = "회원 검색", description = "회원을 ID로 검색할 때 사용하는 API")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/member/{memberId}")
    public ResponseEntity<MemberStatusDTO> getMemberById(@PathVariable Long memberId) {
        // AdminService의 getMemberByMemberId 메서드를 호출하여 회원 정보를 가져옴
        MemberStatusDTO memberStatus = adminService.getMemberByMemberId(memberId);
        return ResponseEntity.ok(memberStatus);
    }

    // 회원 닉네임으로 회원 정보 검색
    @Operation(summary = "회원 검색", description = "회원을 닉네임으로 검색할 때 사용하는 API")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/member/nickname/{username}")
    public ResponseEntity<MemberStatusDTO> getMemberByNickname(@PathVariable String username) {
        // AdminService의 getMemberByNickname 메서드를 호출하여 회원 정보를 가져옴
        MemberStatusDTO memberStatus = adminService.getMemberByNickname(username);
        return ResponseEntity.ok(memberStatus);
    }

    // 모든 회원 목록 조회
    @Operation(summary = "회원 조회", description = "회원을 목록을 조회할 때 사용하는 API")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/members")
    public ResponseEntity<Page<MemberStatusDTO>> getAllMembers(PageRequestDTO pageRequestDTO) {
        // AdminService의 getAllMembers 메서드를 호출하여 회원 목록을 가져옴
        Page<MemberStatusDTO> members = adminService.getAllMembers(pageRequestDTO);
        return ResponseEntity.ok(members);
    }

    // 특정 회원의 작성글 목록 조회
    @Operation(summary = "회원 작성글 조회", description = "특정 회원의 작성글 목록을 조회할 때 사용하는 API")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/member/{memberId}/boards")
    public ResponseEntity<Page<BoardResponseDTO>> getBoardsByMemberId(
            @PathVariable Long memberId, PageRequestDTO pageRequestDTO) {
        // AdminService의 getBoardsByMemberId 메서드를 호출하여 작성글 목록을 가져옴
        Page<BoardResponseDTO> boards = adminService.getBoardsByMemberId(memberId, pageRequestDTO);
        return ResponseEntity.ok(boards);
    }

    // 특정 회원의 댓글 목록 조회
    @Operation(summary = "회원 작성댓글 조회", description = "특정 회원의 작성글 목록을 조회할 때 사용하는 API")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/member/{memberId}/comments")
    public ResponseEntity<Page<CommentResponseDTO>> getCommentsByMemberId(
            @PathVariable Long memberId, PageRequestDTO pageRequestDTO) {
        // AdminService의 getCommentsByMemberId 메서드를 호출하여 댓글 목록을 가져옴
        Page<CommentResponseDTO> comments = adminService.getCommentsByMemberId(memberId, pageRequestDTO);
        return ResponseEntity.ok(comments);
    }
}