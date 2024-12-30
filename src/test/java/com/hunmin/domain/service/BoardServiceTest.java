//package com.hunmin.domain.service;
//
//import com.hunmin.domain.board.dto.BoardRequestDTO;
//import com.hunmin.domain.board.dto.BoardResponseDTO;
//import com.hunmin.domain.dto.page.PageRequestDTO;
//import com.hunmin.domain.board.entity.Board;
//import com.hunmin.domain.member.entity.Member;
//import com.hunmin.domain.board.repository.BoardRepository;
//import com.hunmin.domain.member.repository.MemberRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//public class BoardServiceTest {
//
//    @InjectMocks
//    private BoardService boardService;
//
//    @Mock
//    private MemberRepository memberRepository;
//
//    @Mock
//    private BoardRepository boardRepository;
//
//    private Member member;
//    private Board board;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        member = new Member(1L, "테스터");
//        board = new Board(1L, member, "테스트 제목", "테스트 내용");
//    }
//
//    // 게시글 등록
//    @Test
//    public void testCreateBoard() {
//        BoardRequestDTO boardRequestDTO = new BoardRequestDTO(null, 1L, "테스트 제목", "테스트 내용", null, null, null);
//
//        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
//        when(boardRepository.save(any(Board.class))).thenReturn(board);
//
//        BoardResponseDTO response = boardService.createBoard(boardRequestDTO);
//
//        assertNotNull(response);
//        assertEquals("테스트 제목", response.getTitle());
//        verify(boardRepository, times(1)).save(any(Board.class));
//    }
//
//    // 게시글 조회
//    @Test
//    public void testReadBoard() {
//        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
//
//        BoardResponseDTO response = boardService.readBoard(1L);
//
//        assertNotNull(response);
//        assertEquals("테스트 제목", response.getTitle());
//    }
//
//    // 게시글 수정
//    @Test
//    public void testUpdateBoard() {
//        BoardRequestDTO boardRequestDTO = new BoardRequestDTO(null, 1L, "수정된 제목", "수정된 내용", null, null, null);
//
//        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
//        when(boardRepository.save(any(Board.class))).thenAnswer(invocation -> {
//            Board updatedBoard = invocation.getArgument(0);
//            return new Board(updatedBoard.getBoardId(), updatedBoard.getMember(), updatedBoard.getTitle(), updatedBoard.getContent());
//        });
//
//        BoardResponseDTO response = boardService.updateBoard(1L, boardRequestDTO);
//
//        assertNotNull(response);
//        assertEquals("수정된 제목", response.getTitle());
//        verify(boardRepository, times(1)).save(any(Board.class));
//    }
//
//    // 게시글 삭제
//    @Test
//    public void testDeleteBoard() {
//        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
//
//        BoardResponseDTO response = boardService.deleteBoard(1L);
//
//        assertNotNull(response);
//        assertEquals("테스트 제목", response.getTitle());
//        verify(boardRepository, times(1)).delete(any(Board.class));
//    }
//
//    // 게시글 목록 조회
//    @Test
//    public void testReadBoardList() {
//        Page<Board> boardPage = new PageImpl<>(List.of(board));
//        when(boardRepository.findAll(any(Pageable.class))).thenReturn(boardPage);
//
//        Page<BoardResponseDTO> response = boardService.readBoardList(PageRequestDTO.builder().build());
//
//        assertNotNull(response);
//        assertEquals(1, response.getTotalElements());
//    }
//
//    // 회원 별 작성글 목록 조회
//    @Test
//    public void testReadBoardListByMember() {
//        Page<Board> boardPage = new PageImpl<>(List.of(board));
//        when(boardRepository.findByMemberId(eq(1L), any(Pageable.class))).thenReturn(boardPage);
//
//        Page<BoardResponseDTO> response = boardService.readBoardListByMember(1L, PageRequestDTO.builder().build());
//
//        assertNotNull(response);
//        assertEquals(1, response.getTotalElements());
//    }
//}
