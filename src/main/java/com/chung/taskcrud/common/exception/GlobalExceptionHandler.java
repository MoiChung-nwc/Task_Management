package com.chung.taskcrud.common.exception;

import com.chung.taskcrud.common.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private String traceId() {
        return UUID.randomUUID().toString();
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Object>> handleAppException(AppException ex, HttpServletRequest req) {
        ErrorCode ec = ex.getErrorCode();
        String tid = traceId();

        if (ec.getStatus().is4xxClientError()) {
            log.warn("[{}] Business error {}: {}", tid, req.getRequestURI(), ex.getMessage());
        } else {
            log.error("[{}] System error {}: {}", tid, req.getRequestURI(), ex.getMessage(), ex);
        }

        return ResponseEntity.status(ec.getStatus())
                .body(ApiResponse.error(ec, ex.getMessage(), req.getRequestURI(), tid));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String tid = traceId();
        String msg = ex.getBindingResult().getAllErrors()
                .stream()
                .map(e -> e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("[{}] Validation {}: {}", tid, req.getRequestURI(), msg);

        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ErrorCode.VALIDATION_ERROR, msg, req.getRequestURI(), tid));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        String tid = traceId();
        String msg = String.format("Invalid value '%s' for parameter '%s'", ex.getValue(), ex.getName());

        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ErrorCode.VALIDATION_ERROR, msg, req.getRequestURI(), tid));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        String tid = traceId();
        return ResponseEntity.status(ErrorCode.METHOD_NOT_ALLOWED.getStatus())
                .body(ApiResponse.error(ErrorCode.METHOD_NOT_ALLOWED, "Method not allowed", req.getRequestURI(), tid));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleUnexpected(Exception ex, HttpServletRequest req) {
        String tid = traceId();
        log.error("[{}] Unexpected {}: {}", tid, req.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage(), req.getRequestURI(), tid));
    }
}