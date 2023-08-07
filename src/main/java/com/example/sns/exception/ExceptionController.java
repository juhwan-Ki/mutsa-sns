package com.example.sns.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> commonExceptionHandler(CommonException e) {
        return ResponseEntity.status(e.getErrorCode().getResultCode().getHttpStatus())
                .body(ErrorResponse.builder()
                        .errorCode(e.getErrorCode().name())
                        .message(e.getMessage())
                        .build());
    }
}