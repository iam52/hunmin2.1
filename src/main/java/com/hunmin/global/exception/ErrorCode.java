package com.hunmin.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // 멤버 관련
    MEMBER_NOT_FOUND("회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND), // 404
    MEMBER_ACCESS_DENIED("회원 접근 권한이 없습니다.", HttpStatus.FORBIDDEN), // 403
    MEMBER_ALREADY_EXIST("이미 존재하는 회원입니다.", HttpStatus.CONFLICT), // 409
    MEMBER_CREATE_FAIL("회원 생성에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    MEMBER_INVALID_INPUT("회원 정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST), // 400

    // 게시글 관련
    BOARD_NOT_FOUND("게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND), // 404
    BOARD_ACCESS_DENIED("게시글 접근 권한이 없습니다.", HttpStatus.FORBIDDEN), // 403
    BOARD_ALREADY_EXIST("이미 존재하는 게시글입니다.", HttpStatus.CONFLICT), // 409
    BOARD_CREATE_FAIL("게시글 생성에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    BOARD_UPDATE_FAIL("게시글 수정에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    BOARD_DELETE_FAIL("게시글 삭제에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    BOARD_INVALID_INPUT("게시글 정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST), // 400

    // 댓글 관련
    COMMENT_NOT_FOUND("댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND), // 404
    COMMENT_ACCESS_DENIED("댓글 접근 권한이 없습니다.", HttpStatus.FORBIDDEN), // 403
    COMMENT_ALREADY_EXIST("이미 존재하는 댓글입니다.", HttpStatus.CONFLICT), // 409
    COMMENT_CREATE_FAIL("댓글 생성에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    COMMENT_UPDATE_FAIL("댓글 수정에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    COMMENT_DELETE_FAIL("댓글 삭제에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    COMMENT_INVALID_INPUT("댓글 정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST), // 400

    // 북마크 관련
    BOOKMARK_NOT_FOUND("북마크를 찾을 수 없습니다.", HttpStatus.NOT_FOUND), // 404
    BOOKMARK_ACCESS_DENIED("북마크 접근 권한이 없습니다.", HttpStatus.FORBIDDEN), // 403
    BOOKMARK_ALREADY_EXIST("이미 존재하는 북마크입니다.", HttpStatus.CONFLICT), // 409
    BOOKMARK_CREATE_FAIL("북마크 생성에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    BOOKMARK_UPDATE_FAIL("북마크 수정에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    BOOKMARK_DELETE_FAIL("북마크 삭제에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    BOOKMARK_INVALID_INPUT("북마크 정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST), // 400

    // 채팅 메시지 관련
    CHAT_MESSAGE_NOT_FOUND("메시지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND), // 404
    CHAT_MESSAGE_SEND_FAIL("메시지 발송에 실패했습니다.", HttpStatus.BAD_GATEWAY), // 502
    CHAT_MESSAGE_ACCESS_DENIED("메시지 접근 권한이 없습니다.", HttpStatus.FORBIDDEN), // 403
    CHAT_MESSAGE_CREATE_FAIL("메시지 생성에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    CHAT_MESSAGE_INVALID_INPUT("메시지 정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST), // 400

    // 채팅방 관련
    CHAT_ROOM_NOT_FOUND("채팅방을 찾을 수 없습니다.", HttpStatus.NOT_FOUND), // 404
    CHAT_ROOM_ACCESS_DENIED("채팅방 접근 권한이 없습니다.", HttpStatus.FORBIDDEN), // 403
    CHAT_ROOM_ALREADY_EXIST("이미 존재하는 채팅방입니다.", HttpStatus.CONFLICT), // 409
    CHAT_ROOM_CREATE_FAIL("채팅방 생성에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    CHAT_ROOM_DELETE_FAIL("채팅방 삭제에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    CHAT_ROOM_INVALID_INPUT("채팅방 정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST), // 400

    // 팔로우 관련
    FOLLOW_NOT_FOUND("팔로우 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND), // 404
    FOLLOW_ACCESS_DENIED("팔로우 접근 권한이 없습니다.", HttpStatus.FORBIDDEN), // 403
    FOLLOW_ALREADY_EXIST("이미 존재하는 팔로우입니다.", HttpStatus.CONFLICT), // 409
    FOLLOW_CREATE_FAIL("팔로우 생성에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    FOLLOW_DELETE_FAIL("팔로우 삭제에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    FOLLOW_INVALID_INPUT("팔로우 정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST), // 400

    // 좋아요 관련
    LIKE_NOT_FOUND("좋아요 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND), // 404
    LIKE_ACCESS_DENIED("좋아요 접근 권한이 없습니다.", HttpStatus.FORBIDDEN), // 403
    LIKE_ALREADY_EXIST("이미 존재하는 좋아요입니다.", HttpStatus.CONFLICT), // 409
    LIKE_CREATE_FAIL("좋아요 생성에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    LIKE_DELETE_FAIL("좋아요 삭제에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    LIKE_INVALID_INPUT("좋아요 정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST), // 400

    // 공지사항 관련
    NOTICE_NOT_FOUND("공지사항을 찾을 수 없습니다.", HttpStatus.NOT_FOUND), // 404
    NOTICE_ACCESS_DENIED("공지사항 접근 권한이 없습니다.", HttpStatus.FORBIDDEN), // 403
    NOTICE_ALREADY_EXIST("이미 존재하는 공지사항입니다.", HttpStatus.CONFLICT), // 409
    NOTICE_CREATE_FAIL("공지사항 생성에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    NOTICE_UPDATE_FAIL("공지사항 수정에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    NOTICE_DELETE_FAIL("공지사항 삭제에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    NOTICE_INVALID_INPUT("공지사항 정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST), // 400

    // 알림 관련
    NOTIFICATION_NOT_FOUND("알림을 찾을 수 없습니다.", HttpStatus.NOT_FOUND), // 404
    NOTIFICATION_SEND_FAIL("알림 발송에 실패했습니다.", HttpStatus.BAD_GATEWAY), // 502
    NOTIFICATION_ACCESS_DENIED("알림 접근 권한이 없습니다.", HttpStatus.FORBIDDEN), // 403
    NOTIFICATION_ALREADY_EXIST("이미 존재하는 알림입니다.", HttpStatus.CONFLICT), // 409
    NOTIFICATION_CREATE_FAIL("알림 생성에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    NOTIFICATION_UPDATE_FAIL("알림 상태 변경에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    NOTIFICATION_INVALID_INPUT("알림 정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST), // 400

    // 단어 관련
    WORD_NOT_FOUND("단어를 찾을 수 없습니다.", HttpStatus.NOT_FOUND), // 404
    WORD_ACCESS_DENIED("단어 접근 권한이 없습니다.", HttpStatus.FORBIDDEN), // 403
    WORD_ALREADY_EXIST("이미 존재하는 단어입니다.", HttpStatus.CONFLICT), // 409
    WORD_CREATE_FAIL("단어 생성에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    WORD_UPDATE_FAIL("단어 수정에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    WORD_DELETE_FAIL("단어 삭제에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    WORD_INVALID_INPUT("단어 정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST); // 400

    private final String message;
    private final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public CustomException throwException() {
        return new CustomException(this);
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}