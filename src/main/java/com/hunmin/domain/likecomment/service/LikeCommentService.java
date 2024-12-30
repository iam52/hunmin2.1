package com.hunmin.domain.likecomment.service;

import com.hunmin.domain.board.entity.Board;
import com.hunmin.domain.comment.dto.CommentResponseDTO;
import com.hunmin.domain.comment.entity.Comment;
import com.hunmin.domain.notification.dto.NotificationSendDTO;
import com.hunmin.domain.likecomment.entity.LikeComment;
import com.hunmin.domain.member.entity.Member;
import com.hunmin.domain.notification.entity.NotificationType;
import com.hunmin.domain.notification.service.NotificationService;
import com.hunmin.global.handler.SseEmitters;
import com.hunmin.domain.comment.repository.CommentRepository;
import com.hunmin.domain.likecomment.repository.LikeCommentRepository;
import com.hunmin.domain.member.repository.MemberRepository;
import com.hunmin.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class LikeCommentService {
    private final LikeCommentRepository likeCommentRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;
    private final SseEmitters sseEmitters;

    //좋아요 등록
    @Transactional
    public void createLikeComment(Long memberId, Long commentId) {
        Member member = memberRepository.findById(memberId).orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);
        Comment comment = commentRepository.findById(commentId).orElseThrow(ErrorCode.COMMENT_NOT_FOUND::throwException);
        Long commentMemberId = comment.getMember().getMemberId();
        Board board = comment.getBoard();

        likeCommentRepository.findByMemberAndComment(member, comment).ifPresentOrElse(
                likeComment -> {
                    throw ErrorCode.LIKE_CREATE_FAIL.throwException();
                },
                () -> {
                    likeCommentRepository.save(LikeComment.builder().member(member).comment(comment).build());
                    comment.incrementLikeCount();
                    if (!commentMemberId.equals(memberId)) {
                        NotificationSendDTO notificationSendDTO = NotificationSendDTO.builder()
                                .memberId(commentMemberId)
                                .message("[" + board.getTitle() + "] 에 작성한 댓글 " + "'" + comment.getContent() +"'에 " + member.getNickname() +" 님의 좋아요")
                                .notificationType(NotificationType.LIKE)
                                .url("/board/" + board.getBoardId())
                                .build();

                        notificationService.send(notificationSendDTO);
                    }

                    String emitterId = commentMemberId + "_";
                    SseEmitter emitter = sseEmitters.findSingleEmitter(emitterId);

                    if (emitter != null) {
                        try {
                            emitter.send(new CommentResponseDTO(comment));
                        } catch (IOException e) {
                            log.error("Error sending comment to client via SSE: {}", e.getMessage());
                            sseEmitters.delete(emitterId);
                        }
                    }
                }
        );
    }

    //좋아요 삭제
    @Transactional
    public void deleteLikeComment(Long memberId, Long commentId) {
        Member member = memberRepository.findById(memberId).orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);
        Comment comment = commentRepository.findById(commentId).orElseThrow(ErrorCode.COMMENT_NOT_FOUND::throwException);
        LikeComment likeComment = likeCommentRepository.findByMemberAndComment(member, comment)
                .orElseThrow(ErrorCode.LIKE_NOT_FOUND::throwException);

        try {
            likeCommentRepository.delete(likeComment);
            comment.decrementLikeCount();
        } catch (Exception e) {
            throw ErrorCode.LIKE_DELETE_FAIL.throwException();
        }
    }

    //좋아요 여부 확인
    public boolean isLikeComment(Long memberId, Long commentId) {
        Member member = memberRepository.findById(memberId).orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);
        Comment comment = commentRepository.findById(commentId).orElseThrow(ErrorCode.COMMENT_NOT_FOUND::throwException);

        return likeCommentRepository.existsByMemberAndComment(member, comment);
    }

    //좋아요 누른 사용자 목록 조회
    public List<Map<String, String>> getLikeCommentMembers(Long commentId) {
        List<Member> members = likeCommentRepository.findMembersByLikedCommentId(commentId);
        return members.stream()
                .map(member -> {
                    Map<String, String> memberInfo = new HashMap<>();
                    memberInfo.put("nickname", member.getNickname());
                    memberInfo.put("image", member.getImage());
                    return memberInfo;
                })
                .collect(Collectors.toList());
    }
}
