package com.hunmin.domain.bookmark.repository;

import com.hunmin.domain.bookmark.entity.Bookmark;
import com.hunmin.domain.board.entity.Board;
import com.hunmin.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    //사용자와 게시글로 북마크 조회
    Optional<Bookmark> findByMemberAndBoard(Member member, Board board);

    //회원 별 북마크 게시글 목록 조회
    @Query("SELECT b.board FROM Bookmark b WHERE b.member.memberId = :memberId ORDER BY b.board.boardId DESC")
    List<Board> findByMemberId(@Param("memberId") Long memberId);

    //북마크 여부 확인
    boolean existsByMemberAndBoard(Member member, Board board);
}
