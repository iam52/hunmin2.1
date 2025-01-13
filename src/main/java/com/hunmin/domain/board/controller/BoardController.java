package com.hunmin.domain.board.controller;

import com.hunmin.domain.board.dto.BoardRequestDTO;
import com.hunmin.domain.board.dto.BoardResponseDTO;
import com.hunmin.domain.board.dto.PostImageResponse;
import com.hunmin.domain.board.repository.BoardRepository;
import com.hunmin.domain.board.service.BoardService;
import com.hunmin.domain.member.repository.MemberRepository;
import com.hunmin.global.common.PageRequestDTO;
import com.hunmin.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
@Tag(name = "게시글", description = "게시글 CRUD")
public class BoardController {
    private final BoardService boardService;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    //게시글 등록
    @PostMapping
    @Operation(summary = "게시글 등록", description = "게시글을 등록할 때 사용하는 API")
    public ResponseEntity<BoardResponseDTO> createBoard(@RequestBody BoardRequestDTO boardRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.createBoard(boardRequestDTO));
    }

    //게시글 조회
    @GetMapping("/{boardId}")
    @Operation(summary = "게시글 조회", description = "게시글을 조회할 때 사용하는 API")
    public ResponseEntity<BoardResponseDTO> readBoard(@PathVariable Long boardId) {
        return ResponseEntity.status(HttpStatus.OK).body(boardService.readBoard(boardId));
    }

    //게시글 목록 조회
    @GetMapping
    @Operation(summary = "게시글 목록", description = "게시글 목록을 조회할 때 사용하는 API")
    public ResponseEntity<Page<BoardResponseDTO>> readBoardList(@RequestParam(value = "page", defaultValue = "1") int page,
                                                                @RequestParam(value = "size", defaultValue = "5") int size) {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().page(page).size(size).build();
        return ResponseEntity.status(HttpStatus.OK).body(boardService.readBoardList(pageRequestDTO));
    }

    //회원 별 게시글 목록 조회
    @GetMapping("/member/{memberId}")
    @Operation(summary = "회원 별 작성글 목록", description = "회원별 작성글 목록을 조회할 때 사용하는 API")
    public ResponseEntity<Page<BoardResponseDTO>> readBoardList(@PathVariable Long memberId,
                                                                @RequestParam(value = "page", defaultValue = "1") int page,
                                                                @RequestParam(value = "size", defaultValue = "10") int size) {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().page(page).size(size).build();
        return ResponseEntity.status(HttpStatus.OK).body(boardService.readBoardListByMember(memberId, pageRequestDTO));
    }

    //게시글 수정
    @PutMapping("/{boardId}")
    @Operation(summary = "게시글 수정", description = "게시글을 수정할 때 사용하는 API")
    public ResponseEntity<BoardResponseDTO> updateBoard(@PathVariable Long boardId,
                                                        @RequestBody BoardRequestDTO boardRequestDTO,
                                                        Authentication authentication) {
        Long id = memberRepository.findByEmail(authentication.getName()).get().getMemberId();
        if (!id.equals(boardRequestDTO.getMemberId())) {
            throw ErrorCode.BOARD_UPDATE_FAIL.throwException();
        }
        return ResponseEntity.status(HttpStatus.OK).body(boardService.updateBoard(boardId, boardRequestDTO));
    }

    //게시글 삭제
    @DeleteMapping("/{boardId}")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제할 때 사용하는 API")
    public ResponseEntity<Map<String, String>> deleteBoard(@PathVariable Long boardId, Authentication authentication) {
        Long id = memberRepository.findByEmail(authentication.getName()).get().getMemberId();
        if (!id.equals(boardRepository.findById(boardId).get().getMember().getMemberId())) {
            throw ErrorCode.BOARD_DELETE_FAIL.throwException();
        }
        boardService.deleteBoard(boardId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //게시글 이미지 첨부
    @PostMapping(value = "/{boardId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "게시글 이미지 등록", description = "게시글에 여러 이미지를 등록할 때 사용하는 API")
    public ResponseEntity<PostImageResponse> uploadImages(@RequestParam("boardId") Long boardId,
                                                          @RequestPart("files") List<MultipartFile> multipartFiles
    ) throws IOException {
        PostImageResponse response = boardService.uploadPostImage(boardId, multipartFiles);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
