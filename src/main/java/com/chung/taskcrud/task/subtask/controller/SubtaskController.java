package com.chung.taskcrud.task.subtask.controller;

import com.chung.taskcrud.common.dto.response.ApiResponse;
import com.chung.taskcrud.task.subtask.dto.request.CreateSubtaskRequest;
import com.chung.taskcrud.task.subtask.dto.request.UpdateSubtaskRequest;
import com.chung.taskcrud.task.subtask.dto.response.SubtaskResponse;
import com.chung.taskcrud.task.subtask.service.SubtaskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks/{taskId}/subtasks")
public class SubtaskController {

    private final SubtaskService subtaskService;

    private String traceId() {
        return UUID.randomUUID().toString();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubtaskResponse>> create(
            Authentication authentication,
            @PathVariable Long taskId,
            @Valid @RequestBody CreateSubtaskRequest request,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        SubtaskResponse data = subtaskService.create(authentication, actorId, taskId, request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @PutMapping("/{subtaskId}")
    public ResponseEntity<ApiResponse<SubtaskResponse>> update(
            Authentication authentication,
            @PathVariable Long taskId,
            @PathVariable Long subtaskId,
            @Valid @RequestBody UpdateSubtaskRequest request,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        SubtaskResponse data = subtaskService.update(authentication, actorId, taskId, subtaskId, request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @DeleteMapping("/{subtaskId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            Authentication authentication,
            @PathVariable Long taskId,
            @PathVariable Long subtaskId,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        subtaskService.delete(authentication, actorId, taskId, subtaskId);
        return ResponseEntity.ok(ApiResponse.success(null, http.getRequestURI(), traceId()));
    }

    @GetMapping("/{subtaskId}")
    public ResponseEntity<ApiResponse<SubtaskResponse>> detail(
            Authentication authentication,
            @PathVariable Long taskId,
            @PathVariable Long subtaskId,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        SubtaskResponse data = subtaskService.detail(authentication, actorId, taskId, subtaskId);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }
}
