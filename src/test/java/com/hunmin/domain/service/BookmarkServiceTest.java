package com.hunmin.domain.service;

import com.hunmin.domain.dto.board.BoardResponseDTO;
import com.hunmin.domain.entity.Board;
import com.hunmin.domain.entity.Bookmark;
import com.hunmin.domain.entity.Member;
import com.hunmin.domain.repository.BoardRepository;
import com.hunmin.domain.repository.BookmarkRepository;
import com.hunmin.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookmarkServiceTest {
    @InjectMocks
    private BookmarkService bookmarkService;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private MemberRepository memberRepository;

    private Member member;
    private Board board;
    private Bookmark bookmark;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Member member = Member.builder()
                .email("test@example.com")
                .password("password123")
                .nickname("testuser")
                .country("Korea")
                .build();
        Board board = Board.builder()
                .member(member)
                .title("게시글 제목")
                .content("게시글 내용")
                .location("서울")
                .latitude(37.5665)
                .longitude(126.9780)
                .build();
        bookmark = new Bookmark(1L, member, board);
    }

    //북마크 등록 테스트
    @Test
    void createBookmark() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        when(bookmarkRepository.findByMemberAndBoard(any(Member.class), any(Board.class))).thenReturn(Optional.empty());

        bookmarkService.createBookmark(1L, 1L);

        verify(bookmarkRepository, times(1)).save(any(Bookmark.class));
    }

    //북마크 삭제 테스트
    @Test
    void deleteBookmark() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        when(bookmarkRepository.findByMemberAndBoard(any(Member.class), any(Board.class))).thenReturn(Optional.of(bookmark));

        bookmarkService.deleteBookmark(1L, 1L);

        verify(bookmarkRepository, times(1)).delete(any(Bookmark.class));
    }

    //회원 별 북마크 조회 테스트
    @Test
    void readBookmarkByMember() {
        when(bookmarkRepository.findByMemberId(1L)).thenReturn(List.of(board));

        List<BoardResponseDTO> response = bookmarkService.readBookmarkByMember(1L);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    //북마크 여부 확인 테스트
    @Test
    void isBookmarked() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        when(bookmarkRepository.existsByMemberAndBoard(any(Member.class), any(Board.class))).thenReturn(false);

        boolean isBookmarked = bookmarkService.isBookmarked(1L, 1L);

        assertFalse(isBookmarked);
    }
}