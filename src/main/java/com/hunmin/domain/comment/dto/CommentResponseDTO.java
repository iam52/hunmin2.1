package com.hunmin.domain.comment.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.hunmin.domain.comment.entity.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class CommentResponseDTO {
    private Long commentId;
    private Long boardId;
    private Long memberId;
    private String content;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;

    private String nickname;
    private String profileImage;
    private List<CommentResponseDTO> children;
    private int likeCount;

    public CommentResponseDTO(Comment comment) {
        this.commentId = comment.getCommentId();
        this.boardId = comment.getBoard().getBoardId();
        this.memberId = comment.getMember().getMemberId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.nickname = comment.getMember().getNickname();
        this.profileImage = comment.getMember().getImage();
        this.children = (comment.getChildren() != null) ? comment.getChildren().stream()
                .sorted(Comparator.comparing(Comment::getCommentId))
                .map(CommentResponseDTO::new)
                .collect(Collectors.toList()) : new ArrayList<>();
        this.likeCount = comment.getLikeCount();
    }
}
