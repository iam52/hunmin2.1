package com.hunmin.domain.service;

import com.hunmin.domain.board.entity.Board;
import com.hunmin.domain.comment.entity.Comment;
import com.hunmin.domain.likecomment.entity.LikeComment;
import com.hunmin.domain.member.entity.Member;
import com.hunmin.domain.comment.repository.CommentRepository;
import com.hunmin.domain.likecomment.repository.LikeCommentRepository;
import com.hunmin.domain.likecomment.service.LikeCommentService;
import com.hunmin.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LikeCommentServiceTest {
    @InjectMocks
    private LikeCommentService likeCommentService;

    @Mock
    private LikeCommentRepository likeCommentRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CommentRepository commentRepository;

    private Member member;
    private Member member2;
    private Board board;
    private Comment comment;
    private LikeComment likeComment;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Member member1 = Member.builder()
                .email("test1@example.com")
                .password("password123")
                .nickname("testuser1")
                .country("Korea")
                .build();
        Member member2 = Member.builder()
                .email("test2@example.com")
                .password("password123")
                .nickname("testuser2")
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
        comment = new Comment(1L, member, board, "테스트 댓글");
        likeComment = new LikeComment(1L, member, comment);
    }

    // 좋아요 등록 테스트
    @Test
    void createLikeComment() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(likeCommentRepository.findByMemberAndComment(any(Member.class), any(Comment.class))).thenReturn(Optional.empty());

        likeCommentService.createLikeComment(1L, 1L);

        verify(likeCommentRepository, times(1)).save(any(LikeComment.class));
    }

    //좋아요 삭제 테스트
    @Test
    void deleteLikeComment() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(likeCommentRepository.findByMemberAndComment(any(Member.class), any(Comment.class))).thenReturn(Optional.of(likeComment));

        likeCommentService.deleteLikeComment(1L, 1L);

        verify(likeCommentRepository, times(1)).delete(any(LikeComment.class));
    }

    //좋아요 여부 확인 테스트
    @Test
    void isLikeComment() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(likeCommentRepository.existsByMemberAndComment(any(Member.class), any(Comment.class))).thenReturn(true);

        boolean isLiked = likeCommentService.isLikeComment(1L, 1L);

        assertTrue(isLiked);
    }

    //좋아요 누른 사용자 목록 조회 테스트
    @Test
    void getLikeCommentMembers() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(likeCommentRepository.findMembersByLikedCommentId(comment.getCommentId()))
                .thenReturn(List.of(member, member2));

        List<Map<String, String>> likedMembersNicknames = likeCommentService.getLikeCommentMembers(comment.getCommentId());

        assertEquals(2, likedMembersNicknames.size());
        assertTrue(likedMembersNicknames.contains("테스터"));
        assertTrue(likedMembersNicknames.contains("테스터2"));
    }
}
