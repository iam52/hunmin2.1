package com.hunmin.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값은 json 변환 시 제외
public class ErrorResponse {
    private final int status;
    private final String message;
    private final LocalDateTime time;
    private String stackTrace;
    private List<ValidationError> validErrors;

    @Data
    @RequiredArgsConstructor
    private static class ValidationError {
        private final String field;
        private final String message;
    }

    public void addValidationError(String field, String message) {
        if (Objects.isNull(validErrors)) {
            validErrors = new ArrayList<>();
        }
        validErrors.add(new ValidationError(field, message));
    }
}
