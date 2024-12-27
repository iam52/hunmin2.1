package com.hunmin.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // 멤버 관련
    MEMBER_NOT_FOUND("회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND), // 404

    // 게시글 관련
    BOARD_NOT_FOUND("게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND), // 404
    BOARD_UPDATE_FAIL("게시글 수정에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    BOARD_DELETE_FAIL("게시글 삭제에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400

    // 댓글 관련
    COMMENT_NOT_FOUND("댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND), // 404
    COMMENT_UPDATE_FAIL("댓글 수정에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    COMMENT_DELETE_FAIL("댓글 삭제에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400

    // 북마크 관련
    BOOKMARK_NOT_FOUND("북마크를 찾을 수 없습니다.", HttpStatus.NOT_FOUND), // 404
    BOOKMARK_UPDATE_FAIL("북마크 수정에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    BOOKMARK_DELETE_FAIL("북마크 삭제에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400

    // 채팅 관련
    CHAT_MESSAGE_NOT_FOUND("메시지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND), // 404
    CHAT_MESSAGE_SEND_FAIL("메시지 발송에 실패했습니다.", HttpStatus.BAD_GATEWAY), // 502

    CHAT_ROOM_NOT_FOUND("채팅방을 찾을 수 없습니다.", HttpStatus.NOT_FOUND), // 404
    CHAT_ROOM_DELETE_FAIL("채팅방 삭제에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400

    // 팔로우 관련
    FOLLOW_NOT_FOUND("팔로우 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND), // 404
    FOLLOW_DELETE_FAIL("팔로우 삭제에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400

    // 좋아요 관련
    LIKE_NOT_FOUND("좋아요 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND), // 404
    LIKE_DELETE_FAIL("좋아요 삭제에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400

    // 공지사항 관련
    NOTICE_NOT_FOUND("공지사항을 찾을 수 없습니다.", HttpStatus.NOT_FOUND), // 404
    NOTICE_UPDATE_FAIL("공지사항 수정에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    NOTICE_DELETE_FAIL("공지사항 삭제에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400

    // 알림 관련
    NOTIFICATION_NOT_FOUND("알림을 찾을 수 없습니다.", HttpStatus.NOT_FOUND), // 404
    NOTIFICATION_SEND_FAIL("알림 발송에 실패했습니다.", HttpStatus.BAD_GATEWAY), // 502
    NOTIFICATION_UPDATE_FAIL("알림 상태 변경에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400

    // 단어 관련
    WORD_NOT_FOUND("단어를 찾을 수 없습니다.", HttpStatus.NOT_FOUND), // 404
    WORD_UPDATE_FAIL("단어 수정에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    WORD_DELETE_FAIL("단어 삭제에 실패했습니다.", HttpStatus.BAD_REQUEST); // 400

    private final String message;
    private final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public RestApiException throwException() {
        return new RestApiException(this);
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}