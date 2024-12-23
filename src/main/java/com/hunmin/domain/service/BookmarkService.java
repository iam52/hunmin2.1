package com.hunmin.domain.service;

import com.hunmin.domain.dto.board.BoardResponseDTO;
import com.hunmin.domain.entity.Bookmark;
import com.hunmin.domain.entity.Board;
import com.hunmin.domain.entity.Member;
import com.hunmin.domain.repository.BookmarkRepository;
import com.hunmin.domain.repository.BoardRepository;
import com.hunmin.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    //북마크 등록
    public void createBookmark(Long boardId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberException.NOT_FOUND::get);
        Board board = boardRepository.findById(boardId).orElseThrow(BoardException.NOT_FOUND::get);

        bookmarkRepository.findByMemberAndBoard(member, board).ifPresentOrElse(
                bookmark -> {
                    throw BookmarkException.NOT_CREATED.get();
                },
                () -> bookmarkRepository.save(Bookmark.builder().member(member).board(board).build())
        );
    }

    //북마크 삭제
    public void deleteBookmark(Long boardId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberException.NOT_FOUND::get);
        Board board = boardRepository.findById(boardId).orElseThrow(BoardException.NOT_FOUND::get);

        Bookmark bookmark = bookmarkRepository.findByMemberAndBoard(member, board).orElseThrow(BookmarkException.NOT_FOUND::get);

        try {
            bookmarkRepository.delete(bookmark);
        } catch (Exception e) {
            throw BookmarkException.NOT_DELETED.get();
        }
    }

    //회원 별 북마크 게시글 목록 조회
    public List<BoardResponseDTO> readBookmarkByMember(Long memberId) {
        List<Board> boards = bookmarkRepository.findByMemberId(memberId);

        return boards.stream()
                .map(BoardResponseDTO::new)
                .collect(Collectors.toList());
    }

    //북마크 여부 확인
    public boolean isBookmarked(Long boardId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberException.NOT_FOUND::get);
        Board board = boardRepository.findById(boardId).orElseThrow(BoardException.NOT_FOUND::get);

        return bookmarkRepository.existsByMemberAndBoard(member, board);
    }
}
