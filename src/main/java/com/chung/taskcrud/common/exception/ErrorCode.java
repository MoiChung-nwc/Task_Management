package com.chung.taskcrud.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    SUCCESS("SYS_000", "Success", HttpStatus.OK),
    INTERNAL_SERVER_ERROR("SYS_001", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR("SYS_002", "Validation failed", HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED("SYS_003", "HTTP method not allowed", HttpStatus.METHOD_NOT_ALLOWED),

    AUTH_INVALID_CREDENTIALS("AUTH_001", "Invalid credentials", HttpStatus.UNAUTHORIZED),
    EMAIL_ALREADY_EXISTS("AUTH_002", "Email already exists", HttpStatus.CONFLICT),
    EMAIL_NOT_VERIFIED("AUTH_003", "Email not verified", HttpStatus.FORBIDDEN),
    VERIFY_TOKEN_INVALID("AUTH_004", "Invalid verification token", HttpStatus.BAD_REQUEST),
    VERIFY_TOKEN_EXPIRED("AUTH_005", "Verification token expired", HttpStatus.BAD_REQUEST),
    VERIFY_TOKEN_USED("AUTH_006", "Verification token already used", HttpStatus.BAD_REQUEST),

    UNAUTHORIZED("AUTH_401", "Unauthorized", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("AUTH_403", "Forbidden", HttpStatus.FORBIDDEN),
    USER_NOT_FOUND("AUTH_404", "User not found", HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND("AUTH_405", "Role not found", HttpStatus.NOT_FOUND),
    PERMISSION_NOT_FOUND("AUTH_406", "Permission not found", HttpStatus.NOT_FOUND),
    ROLE_ALREADY_EXISTS("AUTH_407", "Role already exists", HttpStatus.CONFLICT),
    PERMISSION_ALREADY_EXISTS("AUTH_408", "Permission already exists", HttpStatus.CONFLICT),

    AUTH_CURRENT_PASSWORD_INVALID("AUTH_010", "Current password is incorrect", HttpStatus.BAD_REQUEST),
    AUTH_EMAIL_SAME_AS_OLD("AUTH_011", "New email is same as current email", HttpStatus.BAD_REQUEST),
    AUTH_PASSWORD_SAME_AS_OLD("AUTH_012", "New password must be different", HttpStatus.BAD_REQUEST),

    TASK_NOT_FOUND("TASK_404", "Task not found", HttpStatus.NOT_FOUND),
    TASK_ACCESS_DENIED("TASK_403", "Access denied to this task", HttpStatus.FORBIDDEN),

    SUBTASK_NOT_FOUND("SUBTASK_404", "Subtask not found", HttpStatus.NOT_FOUND),
    SUBTASK_ACCESS_DENIED("SUBTASK_403", "Access denied to this subtask", HttpStatus.FORBIDDEN),

    COMMENT_NOT_FOUND("CMT_404", "Comment not found", HttpStatus.NOT_FOUND),
    COMMENT_ACCESS_DENIED("CMT_403", "Access denied to this comment", HttpStatus.FORBIDDEN),

    NOTIFICATION_NOT_FOUND("NTF_404", "Notification not found", HttpStatus.NOT_FOUND),
    NOTIFICATION_ACCESS_DENIED("NTF_403", "Access denied to this notification", HttpStatus.FORBIDDEN),
    TASK_LOG_NOT_FOUND("LOG_404", "Task log not found", HttpStatus.NOT_FOUND);



    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}