package com.hunmin.domain.comment.repository;

import com.hunmin.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    //게시글 별 댓글 목록 조회
    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.children WHERE c.board.boardId = :boardId AND c.parent IS NULL")
    Page<Comment> findByBoardId(@Param("boardId") Long boardId, Pageable pageable);

    //회원 별 댓글 수 조회
    @Query("SELECT COUNT(*) FROM Comment c WHERE c.member.memberId =:memberId ")
    Integer countByMemberId(Long memberId);

    //회원 별 댓글 목록 조회
    @Query("SELECT c FROM Comment c WHERE c.member.memberId = :memberId")
    Page<Comment> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);
}
