package com.hunmin.domain.likecomment.repository;

import com.hunmin.domain.comment.entity.Comment;
import com.hunmin.domain.likecomment.entity.LikeComment;
import com.hunmin.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeCommentRepository extends JpaRepository<LikeComment, Long> {
    //사용자와 댓글로 좋아요 조회
    Optional<LikeComment> findByMemberAndComment(Member member, Comment comment);

    //좋아요 여부 확인
    boolean existsByMemberAndComment(Member member, Comment comment);

    //좋아요 누른 사용자 목록 조회
    @Query("SELECT l.member FROM LikeComment l WHERE l.comment.commentId = :commentId")
    List<Member> findMembersByLikedCommentId(@Param("commentId") Long commentId);
}
