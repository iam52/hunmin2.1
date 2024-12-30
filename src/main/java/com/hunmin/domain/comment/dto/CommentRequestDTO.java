package com.hunmin.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequestDTO {
    @NotBlank
    private Long commentId;

    @NotBlank
    private Long boardId;

    @NotBlank
    private Long memberId;

    @NotEmpty
    private String content;
}
