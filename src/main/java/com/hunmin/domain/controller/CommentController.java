package com.hunmin.domain.controller;

import com.hunmin.domain.dto.comment.CommentRequestDTO;
import com.hunmin.domain.dto.comment.CommentResponseDTO;
import com.hunmin.domain.dto.page.PageRequestDTO;
import com.hunmin.domain.repository.CommentRepository;
import com.hunmin.domain.repository.MemberRepository;
import com.hunmin.domain.service.CommentService;
import com.hunmin.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/{boardId}/comment")
@Tag(name = "댓글", description = "댓글 CRUD")
public class CommentController {
    private final CommentService commentService;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    //댓글 등록
    @PostMapping
    @Operation(summary = "댓글 등록", description = "댓글을 등록할 때 사용하는 API")
    public ResponseEntity<CommentResponseDTO> createComment(@PathVariable Long boardId, @RequestBody CommentRequestDTO commentRequestDTO) {
        return ResponseEntity.ok(commentService.createComment(boardId, commentRequestDTO));
    }

    //대댓글 등록
    @PostMapping("/{commentId}")
    @Operation(summary = "대댓글 등록", description = "대댓글을 등록할 때 사용하는 API")
    public ResponseEntity<CommentResponseDTO> createCommentChild(@PathVariable Long boardId, @PathVariable Long commentId, @RequestBody CommentRequestDTO commentRequestDTO) {
        return ResponseEntity.ok(commentService.createCommentChild(boardId, commentId, commentRequestDTO));
    }

    //댓글 수정
    @PutMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "댓글을 수정할 때 사용하는 API")
    public ResponseEntity<CommentResponseDTO> updateComment(@PathVariable Long boardId, @PathVariable Long commentId,
                                                            @RequestBody CommentRequestDTO commentRequestDTO, Authentication authentication) {

        Long id = memberRepository.findByEmail(authentication.getName()).getMemberId();

        if(!id.equals(commentRequestDTO.getMemberId())) {
            throw ErrorCode.COMMENT_UPDATE_FAIL.throwException();
        }

        return ResponseEntity.ok(commentService.updateComment(commentId, commentRequestDTO));
    }

    //댓글 삭제
    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제할 때 사용하는 API")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable Long boardId, @PathVariable Long commentId,
                                                             Authentication authentication) {
        Long id = memberRepository.findByEmail(authentication.getName()).getMemberId();

        if(!id.equals(commentRepository.findById(commentId).get().getMember().getMemberId())) {
            throw ErrorCode.COMMENT_DELETE_FAIL.throwException();
        }

        commentService.deleteComment(commentId);
        return ResponseEntity.ok(Map.of("result", "success"));
    }

    //게시글 별 댓글 목록 조회
    @GetMapping
    @Operation(summary = "댓글 목록", description = "댓글 목록을 조회할 때 사용하는 API")
    public ResponseEntity<Page<CommentResponseDTO>> readCommentList(@PathVariable Long boardId,
                                                                    @RequestParam(value = "page", defaultValue = "1") int page,
                                                                    @RequestParam(value = "size", defaultValue = "10") int size) {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().page(page).size(size).build();
        return ResponseEntity.ok(commentService.readCommentList(boardId, pageRequestDTO));
    }
}
