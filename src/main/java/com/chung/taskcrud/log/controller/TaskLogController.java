package com.chung.taskcrud.log.controller;

import com.chung.taskcrud.common.dto.response.ApiResponse;
import com.chung.taskcrud.common.dto.response.PageResponse;
import com.chung.taskcrud.log.dto.response.TaskLogResponse;
import com.chung.taskcrud.log.service.TaskLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks/{taskId}/logs")
public class TaskLogController {

    private final TaskLogService taskLogService;

    private String traceId() {
        return UUID.randomUUID().toString();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TaskLogResponse>>> list(
            Authentication authentication,
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);

        PageResponse<TaskLogResponse> data = taskLogService.list(authentication, actorId, taskId, pageable);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @GetMapping("/{logId}")
    public ResponseEntity<ApiResponse<TaskLogResponse>> detail(
            Authentication authentication,
            @PathVariable Long taskId,
            @PathVariable Long logId,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        TaskLogResponse data = taskLogService.detail(authentication, actorId, taskId, logId);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }
}
