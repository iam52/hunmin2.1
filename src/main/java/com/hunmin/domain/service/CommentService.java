package com.hunmin.domain.service;

import com.hunmin.domain.dto.comment.CommentRequestDTO;
import com.hunmin.domain.dto.comment.CommentResponseDTO;
import com.hunmin.domain.dto.notification.NotificationSendDTO;
import com.hunmin.domain.dto.page.PageRequestDTO;
import com.hunmin.domain.entity.Board;
import com.hunmin.domain.entity.Comment;
import com.hunmin.domain.entity.Member;
import com.hunmin.domain.entity.NotificationType;
import com.hunmin.domain.handler.SseEmitters;
import com.hunmin.domain.repository.BoardRepository;
import com.hunmin.domain.repository.CommentRepository;
import com.hunmin.domain.repository.MemberRepository;
import com.hunmin.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class CommentService {
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;
    private final SseEmitters sseEmitters;

    // 댓글 등록
    public CommentResponseDTO createComment(Long boardId, CommentRequestDTO commentRequestDTO) {
        try {
            Member member = memberRepository.findById(commentRequestDTO.getMemberId())
                    .orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);
            Board board = boardRepository.findById(boardId).orElseThrow(ErrorCode.BOARD_NOT_FOUND::throwException);

            Comment comment = Comment.builder()
                    .member(member)
                    .board(board)
                    .content(commentRequestDTO.getContent())
                    .likeCount(0)
                    .build();

            commentRepository.save(comment);

            Long boardMemberId = comment.getBoard().getMember().getMemberId();

            if (!boardMemberId.equals(comment.getMember().getMemberId())) {
                NotificationSendDTO notificationSendDTO = NotificationSendDTO.builder()
                        .memberId(boardMemberId)
                        .message("[" + board.getTitle() + "] 새로운 댓글")
                        .notificationType(NotificationType.COMMENT)
                        .url("/board/" + boardId)
                        .build();

                notificationService.send(notificationSendDTO);
            }

            String emitterId = boardMemberId + "_";
            SseEmitter emitter = sseEmitters.findSingleEmitter(emitterId);

            if (emitter != null) {
                try {
                    emitter.send(new CommentResponseDTO(comment));
                } catch (IOException e) {
                    log.error("Error sending comment to client via SSE: {}", e.getMessage());
                    sseEmitters.delete(emitterId);
                }
            }

            return new CommentResponseDTO(comment);
        } catch (Exception e) {
            log.error(e);
            throw ErrorCode.COMMENT_CREATE_FAIL.throwException();
        }
    }

    //대댓글 등록
    public CommentResponseDTO createCommentChild(Long boardId, Long commentId, CommentRequestDTO commentRequestDTO) {
        try {
            Member member = memberRepository.findById(commentRequestDTO.getMemberId()).orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);
            Board board = boardRepository.findById(boardId).orElseThrow(ErrorCode.BOARD_NOT_FOUND::throwException);
            Comment parent = commentRepository.findById(commentId).orElseThrow(ErrorCode.COMMENT_NOT_FOUND::throwException);

            Comment children = Comment.builder().member(member).board(board).parent(parent).content(commentRequestDTO.getContent()).likeCount(0).build();

            commentRepository.save(children);

            Long parentMemberId = parent.getMember().getMemberId();

            if (!parentMemberId.equals(children.getMember().getMemberId())) {
                NotificationSendDTO notificationSendDTO = NotificationSendDTO.builder()
                        .memberId(parentMemberId)
                        .message("[" + board.getTitle() + "] 에 작성한 댓글 " + "'" + parent.getContent() +"'에 새로운 대댓글")
                        .notificationType(NotificationType.COMMENT)
                        .url("/board/" + boardId)
                        .build();

                notificationService.send(notificationSendDTO);
            }

            String emitterId = parentMemberId + "_";
            SseEmitter emitter = sseEmitters.findSingleEmitter(emitterId);

            if (emitter != null) {
                try {
                    emitter.send(new CommentResponseDTO(children));
                } catch (IOException e) {
                    log.error("Error sending comment to client via SSE: {}", e.getMessage());
                    sseEmitters.delete(emitterId);
                }
            } else {
                log.warn("Emitter not found for member ID: {}", parentMemberId);
            }

            return new CommentResponseDTO(children);
        } catch (Exception e) {
            log.error(e);
            throw ErrorCode.COMMENT_CREATE_FAIL.throwException();
        }
    }

    //댓글 수정
    public CommentResponseDTO updateComment(Long commentId, CommentRequestDTO commentRequestDTO) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(ErrorCode.COMMENT_NOT_FOUND::throwException);

        try {
            comment.changeContent(commentRequestDTO.getContent());

            return new CommentResponseDTO(comment);
        } catch (Exception e) {
            log.error(e);
            throw ErrorCode.COMMENT_UPDATE_FAIL.throwException();
        }
    }

    //댓글 삭제
    public CommentResponseDTO deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(ErrorCode.COMMENT_NOT_FOUND::throwException);

        try {
            commentRepository.delete(comment);

            return new CommentResponseDTO(comment);
        } catch (Exception e) {
            log.error(e);
            throw ErrorCode.COMMENT_DELETE_FAIL.throwException();
        }
    }

    //게시글 별 댓글 목록 조회
    public Page<CommentResponseDTO> readCommentList(Long boardId, PageRequestDTO pageRequestDTO) {
        Sort sort = Sort.by(Sort.Direction.ASC, "commentId");
        Pageable pageable = pageRequestDTO.getPageable(sort);
        Page<Comment> comments = commentRepository.findByBoardId(boardId, pageable);
        return comments.map(CommentResponseDTO::new);
    }
}
