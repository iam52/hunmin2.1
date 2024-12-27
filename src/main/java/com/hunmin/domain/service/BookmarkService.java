package com.hunmin.domain.service;

import com.hunmin.domain.dto.board.BoardResponseDTO;
import com.hunmin.domain.dto.bookmark.BookmarkResponse;
import com.hunmin.domain.entity.Bookmark;
import com.hunmin.domain.entity.Board;
import com.hunmin.domain.entity.Member;
import com.hunmin.domain.repository.BookmarkRepository;
import com.hunmin.domain.repository.BoardRepository;
import com.hunmin.domain.repository.MemberRepository;
import com.hunmin.global.exception.ErrorCode;
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
    public BookmarkResponse createBookmark(Long boardId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);
        Board board = boardRepository.findById(boardId).orElseThrow(ErrorCode.BOARD_NOT_FOUND::throwException);
        bookmarkRepository.findByMemberAndBoard(member, board)
                .ifPresent(bookmark -> {
                    throw ErrorCode.BOOKMARK_ALREADY_EXIST.throwException();
                });
        Bookmark savedBookmark = bookmarkRepository.save(
                Bookmark.builder()
                        .member(member)
                        .board(board)
                        .build()
        );
        return BookmarkResponse.from(savedBookmark);
    }

    //북마크 삭제
    public void deleteBookmark(Long boardId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);
        Board board = boardRepository.findById(boardId).orElseThrow(ErrorCode.BOARD_NOT_FOUND::throwException);
        Bookmark bookmark = bookmarkRepository.findByMemberAndBoard(member, board).orElseThrow(ErrorCode.BOOKMARK_NOT_FOUND::throwException);
        try {
            bookmarkRepository.delete(bookmark);
        } catch (Exception e) {
            throw ErrorCode.BOOKMARK_DELETE_FAIL.throwException();
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
        Member member = memberRepository.findById(memberId).orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);
        Board board = boardRepository.findById(boardId).orElseThrow(ErrorCode.BOARD_NOT_FOUND::throwException);
        return bookmarkRepository.existsByMemberAndBoard(member, board);
    }
}
