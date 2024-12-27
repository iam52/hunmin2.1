package com.hunmin.global.exception;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@RestControllerAdvice // 전역 예외처리를 위한 어노테이션
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String TRACE = "trace";

    @Value("${error.printStackTrace")
    private boolean printStackTrace;

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception exception, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request
    ) {
        return buildErrorResponse(exception, exception.getMessage(), HttpStatus.valueOf(statusCode.value()), request);
    }

    private ResponseEntity<Object> buildErrorResponse(
            Exception exception, String message, HttpStatus httpStatus, WebRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message, LocalDateTime.now());
        if (printStackTrace && isTraceOn(request)) {
            errorResponse.setStackTrace(ExceptionUtils.getStackTrace(exception));
        }
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    private boolean isTraceOn(WebRequest request) {
        String[] value = request.getParameterValues(TRACE);
        return Objects.nonNull(value) && value.length > 0 && value[0].contentEquals("true");
    }

    // ErrorCode 예외 처리
    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<Object> handleRestApiException(RestApiException exception, WebRequest request) {
        log.error("=== Exception 발생: ", exception);
        return buildErrorResponse(exception, exception.getMessage(), exception.getErrorCode().getStatus(), request);
    }

    // 기타 모든 예외 500 에러 처리
    @ExceptionHandler(Exception.class)
    @Hidden
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtException(Exception exception, WebRequest request) {
        log.error("=== 예상치 못한 에러 발생: ", exception);
        return buildErrorResponse(exception, exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    // 403 에러 예외 처리
    @ExceptionHandler(AccessDeniedException.class)
    @Hidden
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException exception, WebRequest request) {
        log.error("=== 접근 거부: ", exception);
        return buildErrorResponse(exception, exception.getMessage(), HttpStatus.FORBIDDEN, request);
    }

    // 409 에러 예외 처리
    @ExceptionHandler(AlreadyExistElementException.class)
    @Hidden
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleAlreadyExistElementException(AlreadyExistElementException exception, WebRequest request) {
        log.error("이미 존재하는 데이터입니다.", exception);
        return buildErrorResponse(exception, exception.getMessage(), HttpStatus.CONFLICT, request);
    }

    // 412 에러 예외 처리
    @Override
    @Hidden
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(), "입력하신 정보가 올바르지 않습니다.", LocalDateTime.now()
        );
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.unprocessableEntity().body(errorResponse);
    }
}
