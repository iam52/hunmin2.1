package com.hunmin.global.exception;

public enum ErrorCode {

    // 멤버 관련
    MEMBER_NOT_FOUND("회원을 찾을 수 없습니다.", 404),
    MEMBER_DUPLICATE("이미 존재하는 회원입니다.", 409),
    MEMBER_INVALID("잘못된 회원 정보입니다.", 400),
    MEMBER_UNAUTHORIZED("인증에 실패했습니다.", 401),

    // 게시글 관련
    BOARD_NOT_FOUND("게시글을 찾을 수 없습니다.", 404),
    BOARD_CREATE_FAIL("게시글 작성에 실패했습니다.", 400),
    BOARD_UPDATE_FAIL("게시글 수정에 실패했습니다.", 400),
    BOARD_DELETE_FAIL("게시글 삭제에 실패했습니다.", 400),

    // 댓글 관련
    COMMENT_NOT_FOUND("댓글을 찾을 수 없습니다.", 404),
    COMMENT_CREATE_FAIL("댓글 작성에 실패했습니다.", 400),
    COMMENT_UPDATE_FAIL("댓글 수정에 실패했습니다.", 400),
    COMMENT_DELETE_FAIL("댓글 삭제에 실패했습니다.", 400),

    // 북마크 관련
    BOOKMARK_NOT_FOUND("북마크를 찾을 수 없습니다.", 404),
    BOOKMARK_CREATE_FAIL("북마크 등록에 실패했습니다.", 400),
    BOOKMARK_DELETE_FAIL("북마크 삭제에 실패했습니다.", 400),

    // 채팅 관련
    CHAT_ROOM_NOT_FOUND("채팅방을 찾을 수 없습니다.", 404),
    CHAT_ROOM_DUPLICATE("이미 존재하는 채팅방입니다.", 409),
    CHAT_MESSAGE_NOT_FOUND("메시지를 찾을 수 없습니다.", 404),
    CHAT_MESSAGE_SEND_FAIL("메시지 전송에 실패했습니다.", 400),

    // 팔로우 관련
    FOLLOW_NOT_FOUND("팔로우 관계가 존재하지 않습니다.", 404),
    FOLLOW_DUPLICATE("이미 팔로우한 사용자입니다.", 409),
    FOLLOW_INVALID("잘못된 팔로우 요청입니다.", 400),

    // 좋아요 관련
    LIKE_NOT_FOUND("좋아요를 찾을 수 없습니다.", 404),
    LIKE_CREATE_FAIL("좋아요 등록에 실패했습니다.", 400),
    LIKE_DELETE_FAIL("좋아요 취소에 실패했습니다.", 400),

    // 공지사항 관련
    NOTICE_NOT_FOUND("공지사항을 찾을 수 없습니다.", 404),
    NOTICE_CREATE_FAIL("공지사항 작성에 실패했습니다.", 400),
    NOTICE_UPDATE_FAIL("공지사항 수정에 실패했습니다.", 400),
    NOTICE_DELETE_FAIL("공지사항 삭제에 실패했습니다.", 400),
    NOTICE_UNAUTHORIZED("공지사항 권한이 없습니다.", 401),

    // 알림 관련
    NOTIFICATION_NOT_FOUND("알림을 찾을 수 없습니다.", 404),
    NOTIFICATION_SEND_FAIL("알림 전송에 실패했습니다.", 400),
    NOTIFICATION_UPDATE_FAIL("알림 상태 변경에 실패했습니다.", 400),

    // 단어 관련
    WORD_NOT_FOUND("단어를 찾을 수 없습니다.", 404),
    WORD_CREATE_FAIL("단어 등록에 실패했습니다.", 400),
    WORD_UPDATE_FAIL("단어 수정에 실패했습니다.", 400),
    WORD_DELETE_FAIL("단어 삭제에 실패했습니다.", 400);

    private final String message;
    private final int code;

    ErrorCode(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public CustomException throwException() {
        return new CustomException(message, code);
    }
}