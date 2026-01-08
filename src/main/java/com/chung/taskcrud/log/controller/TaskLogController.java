package com.chung.taskcrud.log.controller;

import com.chung.taskcrud.common.dto.response.ApiResponse;
import com.chung.taskcrud.common.dto.response.PageResponse;
import com.chung.taskcrud.log.dto.response.TaskLogResponse;
import com.chung.taskcrud.log.service.TaskLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Tasks - Logs", description = "APIs xem lịch sử thay đổi (logs) của task")
@SecurityRequirement(name = "bearerAuth")
public class TaskLogController {

    private final TaskLogService taskLogService;

    private String traceId() {
        return UUID.randomUUID().toString();
    }

    @Operation(
            summary = "Danh sách logs của task (phân trang)",
            description = "Lấy danh sách log của một task theo taskId, có phân trang."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập / token không hợp lệ",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy task",
                    content = @Content
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TaskLogResponse>>> list(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Task ID", example = "100")
            @PathVariable Long taskId,
            @Parameter(description = "Trang (bắt đầu từ 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Kích thước trang", example = "20")
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);

        PageResponse<TaskLogResponse> data = taskLogService.list(authentication, actorId, taskId, pageable);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @Operation(
            summary = "Chi tiết log",
            description = "Lấy chi tiết một log theo taskId và logId."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập / token không hợp lệ",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy task/log",
                    content = @Content
            )
    })
    @GetMapping("/{logId}")
    public ResponseEntity<ApiResponse<TaskLogResponse>> detail(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Task ID", example = "100")
            @PathVariable Long taskId,
            @Parameter(description = "Log ID", example = "5001")
            @PathVariable Long logId,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        TaskLogResponse data = taskLogService.detail(authentication, actorId, taskId, logId);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }
}
