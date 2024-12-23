package com.hunmin.domain.controller.advice;

import com.hunmin.domain.exception.*;
import com.hunmin.global.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class APIControllerAdvice {
    //게시글 예외 처리
    @ExceptionHandler(BoardTaskException.class)
    public ResponseEntity<Map<String, String>> handleBoardTaskException(BoardTaskException e) {
        Map<String, String> map = Map.of("error", e.getMessage());

        return ResponseEntity.status(e.getCode()).body(map);
    }

    //댓글 예외 처리
    @ExceptionHandler(CommentTaskException.class)
    public ResponseEntity<Map<String, String>> handleCommentTaskException(CommentTaskException e) {
        Map<String, String> map = Map.of("error", e.getMessage());

        return ResponseEntity.status(e.getCode()).body(map);
    }

    //공지 예외 처리
    @ExceptionHandler(NoticeTaskException.class)
    public ResponseEntity<Map<String, String>> handleNoticeTaskException(NoticeTaskException e) {
        Map<String, String> map = Map.of("error", e.getMessage());

        return ResponseEntity.status(e.getCode()).body(map);
    }

    //알림 예외 처리
    @ExceptionHandler(NotificationTaskException.class)
    public ResponseEntity<Map<String, String>> handleNotificationTaskException(NotificationTaskException e) {
        Map<String, String> map = Map.of("error", e.getMessage());

        return ResponseEntity.status(e.getCode()).body(map);
    }

    //채팅 예외 처리
    @ExceptionHandler(ChatMessageTaskException.class)
    public ResponseEntity<Map<String, String>> handleChatMessageTaskException(ChatMessageTaskException e) {
        Map<String, String> map = Map.of("error", e.getMessage());

        return ResponseEntity.status(e.getCode()).body(map);
    }

    //채팅룸 예외 처리
    @ExceptionHandler(ChatRoomTaskException.class)
    public ResponseEntity<Map<String, String>> handleChatRoomTaskException(ChatRoomTaskException e) {
        Map<String, String> map = Map.of("error", e.getMessage());

        return ResponseEntity.status(e.getCode()).body(map);
    }

    // 단어 예외 처리
    @ExceptionHandler(WordTaskException.class)
    public ResponseEntity<Map<String, String>> handleWordTaskException(WordTaskException e) {
        Map<String, String> map = Map.of("error", e.getMessage());

        return ResponseEntity.status(e.getCode()).body(map);
    }

    // 팔로우 예외 처리
    @ExceptionHandler(FollowTaskException.class)
    public ResponseEntity<Map<String, String>> handleFollowTaskException(FollowTaskException e) {
        Map<String, String> map = Map.of("error", e.getMessage());

        return ResponseEntity.status(e.getCode()).body(map);
    }

    // 관리자 예외 처리
    @ExceptionHandler(AdminTaskException.class)
    public ResponseEntity<Map<String, String>> handleAdminTaskException(AdminTaskException e) {
        Map<String, String> map = Map.of("error", e.getMessage());

        return ResponseEntity.status(e.getCode()).body(map);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleProductTaskException(HttpMessageNotReadableException e) {
        Map<String, String> map = new HashMap<>();
        map.put("error", "[JSON]" + " 형식을 확인해주세요");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleProductTaskException(MethodArgumentNotValidException e) {
        Map<String, String> map = new HashMap<>();
        map.put("error", e.getFieldError().getDefaultMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> handleProductTaskException(NoResourceFoundException e) {
        Map<String, String> map = new HashMap<>();
        map.put("error", "URL을 잘못 입력하였습니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleProductTaskException(MethodArgumentTypeMismatchException e) {
        Map<String, String> map = new HashMap<>();
        map.put("error", "입력값 형식을 확인해주세요.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }

    // 멤버 예외 처리
    @ExceptionHandler(MemberTaskException.class)
    public ResponseEntity<Map<String, String>> handleMemberTaskException(MemberTaskException e) {
        Map<String, String> map = Map.of("error", e.getMessage());

        return ResponseEntity.status(e.getCode()).body(map);
    }
}
