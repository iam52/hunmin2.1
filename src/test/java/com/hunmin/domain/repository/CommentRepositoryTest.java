package com.hunmin.domain.repository;

import com.hunmin.domain.board.entity.Board;
import com.hunmin.domain.board.repository.BoardRepository;
import com.hunmin.domain.comment.entity.Comment;
import com.hunmin.domain.comment.repository.CommentRepository;
import com.hunmin.domain.member.entity.Member;
import com.hunmin.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CommentRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CommentRepository commentRepository;

    //댓글 등록 테스트
    @Test
    @Transactional
    @Commit
    public void testCreateComment() {
        Member member = memberRepository.findById(1L).get();
        Board board = boardRepository.findById(1L).get();

        Comment comment = Comment.builder().board(board).member(member).content("댓글 테스트").build();

        Comment savedComment = commentRepository.save(comment);

        assertNotNull(savedComment);
    }

    //대댓글 등록 테스트
    @Test
    @Transactional
    @Commit
    public void testCreateCommentChildren() {
        Member member = memberRepository.findById(1L).get();
        Board board = boardRepository.findById(1L).get();
        Comment parent = commentRepository.findById(1L).get();

        Comment children = Comment.builder().board(board).member(member).parent(parent).content("대댓글 테스트").build();

        commentRepository.save(children);

        Comment savedChildren = commentRepository.save(children);

        assertNotNull(savedChildren);
    }

    //댓글 수정 테스트
    @Test
    @Transactional
    @Commit
    public void testUpdateComment() {
        Long commentId = 1L;
        String content = "댓글 수정 테스트";

        Comment comment = commentRepository.findById(commentId).orElseThrow();

        comment.changeContent(content);

        comment = commentRepository.findById(commentId).orElseThrow();

        assertEquals(content, comment.getContent());
    }

    //댓글 삭제 테스트
    @Test
    @Transactional
    @Commit
    public void testDeleteComment() {
        Long commentId = 1L;

        commentRepository.deleteById(commentId);

        assertTrue(commentRepository.findById(commentId).isEmpty());
    }

    //게시글 별 댓글 목록 조회 테스트
    @Test
    public void testReadCommentList() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Comment> comments = commentRepository.findByBoardId(1L, pageable);

        assertNotNull(comments);

        assertEquals(2, comments.getTotalElements());
    }
}
