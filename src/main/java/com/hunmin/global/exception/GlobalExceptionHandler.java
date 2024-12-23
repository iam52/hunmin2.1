package com.hunmin.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice // 전역 예외처리를 위한 어노테이션
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleMemberTaskException(CustomException e) {
        log.error("=== Exception 발생: {}", e.getMessage());
        ErrorResponse response = new ErrorResponse(e.getMessage(), e.getCode());
        return new ResponseEntity<>(response, HttpStatus.valueOf(e.getCode()));
    }

    // 다른 예외들도 필요하다면 추가
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("=== 예상치 못한 에러 발생: {}", e.getMessage());
        ErrorResponse response = new ErrorResponse("서버 에러가 발생했습니다", 500);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
