package com.chung.taskcrud.common.dto.response;

import com.chung.taskcrud.common.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ApiResponse<T> {

    private final boolean success;
    private final int status;
    private final String code;
    private final String message;
    private final T data;
    private final Instant timestamp;
    private final String path;
    private final String traceId;

    public static <T> ApiResponse<T> success(T data, String path, String traceId) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(ErrorCode.SUCCESS.getStatus().value())
                .code(ErrorCode.SUCCESS.getCode())
                .message(ErrorCode.SUCCESS.getMessage())
                .data(data)
                .timestamp(Instant.now())
                .path(path)
                .traceId(traceId)
                .build();
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, String customMessage, String path, String traceId) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(errorCode.getStatus().value())
                .code(errorCode.getCode())
                .message(customMessage != null && !customMessage.isEmpty()
                        ? customMessage
                        : errorCode.getMessage())
                .data(null)
                .timestamp(Instant.now())
                .path(path)
                .traceId(traceId)
                .build();
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, String path, String traceId) {
        return error(errorCode, errorCode.getMessage(), path, traceId);
    }
}