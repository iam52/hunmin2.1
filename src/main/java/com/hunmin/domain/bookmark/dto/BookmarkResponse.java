package com.hunmin.domain.bookmark.dto;

import com.hunmin.domain.bookmark.entity.Bookmark;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkResponse {
    private int status;
    private String message;
    private Long bookmarkId;
    private Long boardId;

    @Builder
    protected BookmarkResponse(int status, String message, Long bookmarkId, Long boardId) {
        this.status = status;
        this.message = message;
        this.bookmarkId = bookmarkId;
        this.boardId = boardId;
    }

    public static BookmarkResponse from(Bookmark bookmark) {
        return BookmarkResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("북마크가 성공적으로 등록되었습니다.")
                .bookmarkId(bookmark.getBookmarkId())
                .boardId(bookmark.getBoard().getBoardId())
                .build();
    }
}
