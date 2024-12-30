package com.hunmin.domain.repository;

import com.hunmin.domain.comment.entity.Comment;
import com.hunmin.domain.comment.repository.CommentRepository;
import com.hunmin.domain.likecomment.entity.LikeComment;
import com.hunmin.domain.member.entity.Member;
import com.hunmin.domain.likecomment.repository.LikeCommentRepository;
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
class LikeCommentRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeCommentRepository likeCommentRepository;

    //사용자와 댓글로 좋아요 조회 테스트
    @Test
    void findByMemberAndComment() {
        Member member = memberRepository.findById(11L).get();
        Comment comment = commentRepository.findById(11L).get();

        LikeComment likeComment = LikeComment.builder()
                .member(member)
                .comment(comment)
                .build();
        likeCommentRepository.save(likeComment);

        Optional<LikeComment> foundLikeComment = likeCommentRepository.findByMemberAndComment(member, comment);

        assertTrue(foundLikeComment.isPresent());
        assertEquals(member.getMemberId(), foundLikeComment.get().getMember().getMemberId());
        assertEquals(comment.getCommentId(), foundLikeComment.get().getComment().getCommentId());
    }

    //좋아요 여부 확인 테스트
    @Test
    void existsByMemberAndComment() {
        Member member = memberRepository.findById(11L).get();
        Comment comment = commentRepository.findById(11L).get();

        LikeComment likeComment = LikeComment.builder()
                .member(member)
                .comment(comment)
                .build();
        likeCommentRepository.save(likeComment);

        boolean exists = likeCommentRepository.existsByMemberAndComment(member, comment);

        assertTrue(exists);
    }

    //좋아요 누른 사용자 목록 조회 테스트
    @Test
    void findMembersByLikedCommentId() {
        Member member1 = memberRepository.findById(11L).get();
        Member member2 = memberRepository.findById(12L).get();
        Comment comment = commentRepository.findById(11L).get();

        LikeComment likeComment1 = LikeComment.builder().member(member1).comment(comment).build();
        LikeComment likeComment2 = LikeComment.builder().member(member2).comment(comment).build();

        likeCommentRepository.save(likeComment1);
        likeCommentRepository.save(likeComment2);

        List<Member> likedMembers = likeCommentRepository.findMembersByLikedCommentId(comment.getCommentId());

        assertEquals(2, likedMembers.size());
        assertTrue(likedMembers.contains(member1));
        assertTrue(likedMembers.contains(member2));
    }
}
