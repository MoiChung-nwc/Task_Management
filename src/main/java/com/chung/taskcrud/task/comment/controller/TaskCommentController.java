package com.chung.taskcrud.task.comment.controller;

import com.chung.taskcrud.common.dto.response.ApiResponse;
import com.chung.taskcrud.common.dto.response.PageResponse;
import com.chung.taskcrud.task.comment.dto.request.CreateCommentRequest;
import com.chung.taskcrud.task.comment.dto.request.UpdateCommentRequest;
import com.chung.taskcrud.task.comment.dto.response.CommentResponse;
import com.chung.taskcrud.task.comment.service.TaskCommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks/{taskId}/comments")
public class TaskCommentController {

    private final TaskCommentService commentService;

    private String traceId() {
        return UUID.randomUUID().toString();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponse>> create(
            Authentication authentication,
            @PathVariable Long taskId,
            @Valid @RequestBody CreateCommentRequest request,
            HttpServletRequest http
            ) {
        Long actorId = (Long) authentication.getPrincipal();
        CommentResponse data = commentService.create(authentication, actorId, taskId, request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CommentResponse>>> list(
            Authentication authentication,
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);

        PageResponse<CommentResponse> data = commentService.list(authentication, actorId, taskId, pageable);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> detail(
            Authentication authentication,
            @PathVariable Long taskId,
            @PathVariable Long commentId,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        CommentResponse data = commentService.detail(authentication, actorId, taskId, commentId);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> update(
            Authentication authentication,
            @PathVariable Long taskId,
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequest request,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        CommentResponse data = commentService.update(authentication, actorId, taskId, commentId, request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            Authentication authentication,
            @PathVariable Long taskId,
            @PathVariable Long commentId,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        commentService.delete(authentication, actorId, taskId, commentId);
        return ResponseEntity.ok(ApiResponse.success(null, http.getRequestURI(), traceId()));
    }
}
