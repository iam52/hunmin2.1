package com.hunmin.domain.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardRequestDTO {
    @NotBlank
    private Long boardId;

    @NotBlank
    private Long memberId;

    @NotEmpty
    private String title;

    @NotEmpty
    private String content;

    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private List<String> imageUrls;
}
