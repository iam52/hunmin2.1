package com.hunmin.domain.controller;

import com.hunmin.domain.dto.board.BoardResponseDTO;
import com.hunmin.domain.dto.bookmark.BookmarkResponse;
import com.hunmin.global.security.CustomUserDetails;
import com.hunmin.domain.repository.MemberRepository;
import com.hunmin.domain.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmark")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final MemberRepository memberRepository;

    //북마크 등록
    @PostMapping("/{boardId}")
    public ResponseEntity<BookmarkResponse> createBookmark(@PathVariable Long boardId,
                                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        BookmarkResponse response = bookmarkService.createBookmark(boardId, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //북마크 삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<String> deleteBookmark(@PathVariable Long boardId, Authentication authentication) {
        Long memberId = memberRepository.findByEmail(authentication.getName()).orElseThrow().getMemberId();
        bookmarkService.deleteBookmark(boardId, memberId);
        return ResponseEntity.ok("북마크 삭제");
    }

    //회원 별 북마크 게시글 목록 조회
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<BoardResponseDTO>> getBookmarkedBoards(@PathVariable Long memberId) {
        List<BoardResponseDTO> bookmarkedBoards = bookmarkService.readBookmarkByMember(memberId);
        return ResponseEntity.ok(bookmarkedBoards);
    }

    //북마크 여부 확인
    @GetMapping("/{boardId}/member/{memberId}")
    public ResponseEntity<Boolean> isBookmarked(@PathVariable Long boardId, @PathVariable Long memberId) {
        boolean isBookmarked = bookmarkService.isBookmarked(boardId, memberId);
        return ResponseEntity.ok(isBookmarked);
    }
}
