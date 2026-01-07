package com.chung.taskcrud.task.controller;

import com.chung.taskcrud.common.dto.response.ApiResponse;
import com.chung.taskcrud.common.dto.response.PageResponse;
import com.chung.taskcrud.task.dto.request.AssignTaskRequest;
import com.chung.taskcrud.task.dto.request.CreateTaskRequest;
import com.chung.taskcrud.task.dto.request.UpdateTaskRequest;
import com.chung.taskcrud.task.dto.request.UpdateTaskStatusRequest;
import com.chung.taskcrud.task.dto.response.TaskDetailResponse;
import com.chung.taskcrud.task.dto.response.TaskResponse;
import com.chung.taskcrud.task.entity.TaskPriority;
import com.chung.taskcrud.task.entity.TaskStatus;
import com.chung.taskcrud.task.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    private String traceId() {
        return UUID.randomUUID().toString();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> create(
            Authentication authentication,
            @Valid @RequestBody CreateTaskRequest request,
            HttpServletRequest http
            ) {
        Long actorId = (Long) authentication.getPrincipal();
        TaskResponse data = taskService.create(authentication, actorId, request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> update(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        TaskResponse data = taskService.update(authentication, actorId, id, request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            Authentication authentication,
            @PathVariable Long id,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        taskService.softDelete(authentication, actorId, id);
        return ResponseEntity.ok(ApiResponse.success(null, http.getRequestURI(), traceId()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TaskResponse>>> list(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,

            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) LocalDate dueFrom,
            @RequestParam(required = false) LocalDate dueTo,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) Long assigneeId,

            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();

        String[] parts = sort.split(",");
        String sortField = parts[0];
        Sort.Direction dir = (parts.length > 1 && parts[1].equalsIgnoreCase("asc"))
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortField));

        PageResponse<TaskResponse> data = taskService.list(
                authentication, actorId, status, priority, dueFrom, dueTo, tag, assigneeId, pageable
        );
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskDetailResponse>> detail(
            Authentication authentication,
            @PathVariable Long id,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        TaskDetailResponse data = taskService.detail(authentication, actorId, id);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @PutMapping("/{id}/assignee")
    public ResponseEntity<ApiResponse<TaskResponse>> assign(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody AssignTaskRequest request,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        TaskResponse data = taskService.assign(authentication, actorId, id, request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TaskResponse>> updateStatus(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskStatusRequest request,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        TaskResponse data = taskService.updateStatus(authentication, actorId, id, request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }
}
