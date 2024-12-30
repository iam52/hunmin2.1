package com.hunmin.domain.repository;

import com.hunmin.domain.board.entity.Board;
import com.hunmin.domain.board.repository.BoardRepository;
import com.hunmin.domain.bookmark.entity.Bookmark;
import com.hunmin.domain.bookmark.repository.BookmarkRepository;
import com.hunmin.domain.member.entity.Member;
import com.hunmin.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookmarkRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    //사용자와 게시글로 북마크 조회 테스트
    @Test
    void findByMemberAndBoard() {
        Member member = memberRepository.findById(11L).get();
        Board board = boardRepository.findById(11L).get();

        Bookmark bookmark = Bookmark.builder()
                .member(member)
                .board(board)
                .build();
        bookmarkRepository.save(bookmark);

        Optional<Bookmark> foundBookmark = bookmarkRepository.findByMemberAndBoard(member, board);

        assertTrue(foundBookmark.isPresent());
        assertEquals(member.getMemberId(), foundBookmark.get().getMember().getMemberId());
        assertEquals(board.getBoardId(), foundBookmark.get().getBoard().getBoardId());
    }

    //회원 별 북마크 게시글 목록 조회 테스트
    @Test
    void findByMemberId() {
        Member member = memberRepository.findById(11L).get();
        Board board = boardRepository.findById(11L).get();

        Bookmark bookmark = Bookmark.builder().member(member).board(board).build();
        bookmarkRepository.save(bookmark);

        List<Board> bookmarkedBoards = bookmarkRepository.findByMemberId(member.getMemberId());

        assertNotNull(bookmarkedBoards);
        assertEquals(1, bookmarkedBoards.size());
        assertEquals(board.getBoardId(), bookmarkedBoards.get(0).getBoardId());
    }

    //북마크 여부 확인 테스트
    @Test
    void existsByMemberAndBoard() {
        Member member = memberRepository.findById(11L).get();
        Board board = boardRepository.findById(11L).get();

        Bookmark bookmark = Bookmark.builder()
                .member(member)
                .board(board)
                .build();
        bookmarkRepository.save(bookmark);

        boolean exists = bookmarkRepository.existsByMemberAndBoard(member, board);

        assertTrue(exists);
    }
}