package com.hunmin.domain.service;

import com.hunmin.domain.dto.comment.CommentResponseDTO;
import com.hunmin.domain.dto.notification.NotificationSendDTO;
import com.hunmin.domain.entity.*;
import com.hunmin.domain.handler.SseEmitters;
import com.hunmin.domain.repository.CommentRepository;
import com.hunmin.domain.repository.LikeCommentRepository;
import com.hunmin.domain.repository.MemberRepository;
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
        Member member = memberRepository.findById(memberId).orElseThrow(MemberException.NOT_FOUND::get);
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentException.NOT_FOUND::get);
        Long commentMemberId = comment.getMember().getMemberId();
        Board board = comment.getBoard();

        likeCommentRepository.findByMemberAndComment(member, comment).ifPresentOrElse(
                likeComment -> {
                    throw LikeCommentException.NOT_CREATED.get();
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
        Member member = memberRepository.findById(memberId).orElseThrow(MemberException.NOT_FOUND::get);
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentException.NOT_FOUND::get);
        LikeComment likeComment = likeCommentRepository.findByMemberAndComment(member, comment).orElseThrow(LikeCommentException.NOT_FOUND::get);

        try {
            likeCommentRepository.delete(likeComment);
            comment.decrementLikeCount();
        } catch (Exception e) {
            throw LikeCommentException.NOT_DELETED.get();
        }
    }

    //좋아요 여부 확인
    public boolean isLikeComment(Long memberId, Long commentId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberException.NOT_FOUND::get);
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentException.NOT_FOUND::get);

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
