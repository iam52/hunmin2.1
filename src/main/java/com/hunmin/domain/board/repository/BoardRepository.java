package com.hunmin.domain.board.repository;

import com.hunmin.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // 회원 별 작성글 목록 조회
    @Query("SELECT b FROM Board b WHERE b.member.memberId = :memberId")
    Page<Board> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    // 게시글과 댓글을 같이 조회
    @Query("SELECT b FROM Board b LEFT JOIN FETCH b.comments WHERE b.boardId = :boardId")
    Optional<Board> findCommentsByBoardId(@Param("boardId") Long boardId);

    // 회원 별 게시글 수 조회
    @Query("SELECT COUNT(*) FROM Board b  WHERE b.member.memberId =:memberId ")
    Integer countByMemberId(Long memberId);

    // 제목으로 게시글 검색
    @EntityGraph(attributePaths = {"member"}) // N+1 문제 해결?
    Page<Board> findByTitleContaining(String title, Pageable pageable);
}