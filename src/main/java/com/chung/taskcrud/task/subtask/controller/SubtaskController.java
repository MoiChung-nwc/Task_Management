package com.chung.taskcrud.task.subtask.controller;

import com.chung.taskcrud.common.dto.response.ApiResponse;
import com.chung.taskcrud.task.subtask.dto.request.CreateSubtaskRequest;
import com.chung.taskcrud.task.subtask.dto.request.UpdateSubtaskRequest;
import com.chung.taskcrud.task.subtask.dto.response.SubtaskResponse;
import com.chung.taskcrud.task.subtask.service.SubtaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Tasks - Subtasks", description = "APIs quản lý subtask theo task")
@SecurityRequirement(name = "bearerAuth")
public class SubtaskController {

    private final SubtaskService subtaskService;

    private String traceId() {
        return UUID.randomUUID().toString();
    }

    @Operation(
            summary = "Tạo subtask",
            description = "Tạo mới subtask cho một task theo taskId. Yêu cầu Bearer token."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Tạo thành công (API đang trả 200)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation fail",
                    content = @Content
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
    @PostMapping
    public ResponseEntity<ApiResponse<SubtaskResponse>> create(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Task ID", example = "100")
            @PathVariable Long taskId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload tạo subtask",
                    content = @Content(
                            schema = @Schema(implementation = CreateSubtaskRequest.class),
                            examples = @ExampleObject(
                                    name = "Create subtask example",
                                    value = """
                                    {
                                      "title": "Write unit tests",
                                      "description": "Add unit tests for TaskService",
                                      "done": false
                                    }
                                    """
                            )
                    )
            )
            @Valid @RequestBody CreateSubtaskRequest request,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        SubtaskResponse data = subtaskService.create(authentication, actorId, taskId, request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @Operation(
            summary = "Cập nhật subtask",
            description = "Cập nhật subtask theo taskId và subtaskId."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Cập nhật thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation fail",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập / token không hợp lệ",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Không có quyền cập nhật subtask (tuỳ logic)",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy task/subtask",
                    content = @Content
            )
    })
    @PutMapping("/{subtaskId}")
    public ResponseEntity<ApiResponse<SubtaskResponse>> update(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Task ID", example = "100")
            @PathVariable Long taskId,
            @Parameter(description = "Subtask ID", example = "2001")
            @PathVariable Long subtaskId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload cập nhật subtask",
                    content = @Content(
                            schema = @Schema(implementation = UpdateSubtaskRequest.class),
                            examples = @ExampleObject(
                                    name = "Update subtask example",
                                    value = """
                                    {
                                      "title": "Write integration tests",
                                      "description": "Add integration tests for TaskController",
                                      "done": true
                                    }
                                    """
                            )
                    )
            )
            @Valid @RequestBody UpdateSubtaskRequest request,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        SubtaskResponse data = subtaskService.update(authentication, actorId, taskId, subtaskId, request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @Operation(
            summary = "Xóa subtask",
            description = "Xóa subtask theo taskId và subtaskId."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Xóa thành công (API đang trả 200)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập / token không hợp lệ",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Không có quyền xóa subtask (tuỳ logic)",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy task/subtask",
                    content = @Content
            )
    })
    @DeleteMapping("/{subtaskId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Task ID", example = "100")
            @PathVariable Long taskId,
            @Parameter(description = "Subtask ID", example = "2001")
            @PathVariable Long subtaskId,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        subtaskService.delete(authentication, actorId, taskId, subtaskId);
        return ResponseEntity.ok(ApiResponse.success(null, http.getRequestURI(), traceId()));
    }

    @Operation(
            summary = "Chi tiết subtask",
            description = "Lấy chi tiết một subtask theo taskId và subtaskId."
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
                    description = "Không tìm thấy task/subtask",
                    content = @Content
            )
    })
    @GetMapping("/{subtaskId}")
    public ResponseEntity<ApiResponse<SubtaskResponse>> detail(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Task ID", example = "100")
            @PathVariable Long taskId,
            @Parameter(description = "Subtask ID", example = "2001")
            @PathVariable Long subtaskId,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        SubtaskResponse data = subtaskService.detail(authentication, actorId, taskId, subtaskId);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }
}
