package com.hunmin.domain.board.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PostImageResponse {
    private final Long boardId;
    private final List<ImageInfo> images;

    public PostImageResponse(Long boardId, List<ImageInfo> images) {
        this.boardId = boardId;
        this.images = images;
    }

    @Getter
    public static class ImageInfo {
        private final String imageUrl;
        private final int width;
        private final int height;

        public ImageInfo(String imageUrl, int width, int height) {
            this.imageUrl = imageUrl;
            this.width = width;
            this.height = height;
        }
    }
}