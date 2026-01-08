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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Tasks", description = "APIs quản lý task: CRUD, lọc, phân công, cập nhật trạng thái")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    private String traceId() {
        return UUID.randomUUID().toString();
    }

    @Operation(
            summary = "Tạo task",
            description = "Tạo mới một task. Yêu cầu Bearer token."
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
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> create(
            @Parameter(hidden = true) Authentication authentication,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload tạo task",
                    content = @Content(
                            schema = @Schema(implementation = CreateTaskRequest.class),
                            examples = @ExampleObject(
                                    name = "Create task example",
                                    value = """
                                    {
                                      "title": "Implement Swagger docs",
                                      "description": "Add Swagger annotations for all controllers",
                                      "priority": "HIGH",
                                      "dueDate": "2026-01-20",
                                      "tags": ["backend", "swagger"]
                                    }
                                    """
                            )
                    )
            )
            @Valid @RequestBody CreateTaskRequest request,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        TaskResponse data = taskService.create(authentication, actorId, request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @Operation(
            summary = "Cập nhật task",
            description = "Cập nhật thông tin task theo ID."
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
                    responseCode = "404",
                    description = "Không tìm thấy task",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> update(
            @Parameter(description = "Task ID", example = "100")
            @PathVariable Long id,
            @Parameter(hidden = true) Authentication authentication,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload cập nhật task",
                    content = @Content(
                            schema = @Schema(implementation = UpdateTaskRequest.class),
                            examples = @ExampleObject(
                                    name = "Update task example",
                                    value = """
                                    {
                                      "title": "Implement Swagger docs (updated)",
                                      "description": "Add swagger annotations + examples for all endpoints",
                                      "priority": "MEDIUM",
                                      "dueDate": "2026-01-25",
                                      "tags": ["backend", "swagger", "docs"]
                                    }
                                    """
                            )
                    )
            )
            @Valid @RequestBody UpdateTaskRequest request,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        TaskResponse data = taskService.update(authentication, actorId, id, request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @Operation(
            summary = "Xóa task (soft delete)",
            description = "Xóa mềm task theo ID."
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
                    description = "Không có quyền xóa task (tuỳ logic)",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy task",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Task ID", example = "100")
            @PathVariable Long id,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        taskService.softDelete(authentication, actorId, id);
        return ResponseEntity.ok(ApiResponse.success(null, http.getRequestURI(), traceId()));
    }

    @Operation(
            summary = "Danh sách task (phân trang + filter + sort)",
            description = """
                    Lấy danh sách task theo phân trang, có filter:
                    - status, priority
                    - dueFrom, dueTo (yyyy-MM-dd)
                    - tag
                    - assigneeId
                    Sort dạng: `field,asc|desc` (mặc định `createdAt,desc`)
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Query param không hợp lệ (ví dụ sai enum/status/priority, sai format date)",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập / token không hợp lệ",
                    content = @Content
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TaskResponse>>> list(
            @Parameter(hidden = true) Authentication authentication,

            @Parameter(description = "Trang (bắt đầu từ 0)", example = "0", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Kích thước trang", example = "20", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Sort: field,asc|desc", example = "createdAt,desc", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "createdAt,desc") String sort,

            @Parameter(description = "Filter theo status", example = "OPEN", in = ParameterIn.QUERY)
            @RequestParam(required = false) TaskStatus status,
            @Parameter(description = "Filter theo priority", example = "HIGH", in = ParameterIn.QUERY)
            @RequestParam(required = false) TaskPriority priority,

            @Parameter(description = "Lọc dueDate từ ngày (yyyy-MM-dd)", example = "2026-01-01", in = ParameterIn.QUERY)
            @RequestParam(required = false) LocalDate dueFrom,
            @Parameter(description = "Lọc dueDate đến ngày (yyyy-MM-dd)", example = "2026-01-31", in = ParameterIn.QUERY)
            @RequestParam(required = false) LocalDate dueTo,

            @Parameter(description = "Lọc theo tag", example = "backend", in = ParameterIn.QUERY)
            @RequestParam(required = false) String tag,
            @Parameter(description = "Lọc theo assigneeId", example = "5", in = ParameterIn.QUERY)
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

    @Operation(
            summary = "Chi tiết task",
            description = "Lấy chi tiết task theo ID."
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
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskDetailResponse>> detail(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Task ID", example = "100")
            @PathVariable Long id,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        TaskDetailResponse data = taskService.detail(authentication, actorId, id);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @Operation(
            summary = "Phân công assignee cho task",
            description = "Gán người thực hiện (assignee) cho task."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Gán thành công",
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
                    description = "Không có quyền gán (tuỳ logic)",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy task hoặc assignee",
                    content = @Content
            )
    })
    @PutMapping("/{id}/assignee")
    public ResponseEntity<ApiResponse<TaskResponse>> assign(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Task ID", example = "100")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload phân công assignee",
                    content = @Content(
                            schema = @Schema(implementation = AssignTaskRequest.class),
                            examples = @ExampleObject(
                                    name = "Assign example",
                                    value = """
                                    {
                                      "assigneeId": 5
                                    }
                                    """
                            )
                    )
            )
            @Valid @RequestBody AssignTaskRequest request,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        TaskResponse data = taskService.assign(authentication, actorId, id, request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @Operation(
            summary = "Cập nhật status task",
            description = "Cập nhật trạng thái task theo ID."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Cập nhật status thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation fail / status không hợp lệ",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập / token không hợp lệ",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Không có quyền đổi status (tuỳ logic)",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy task",
                    content = @Content
            )
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TaskResponse>> updateStatus(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Task ID", example = "100")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload update status",
                    content = @Content(
                            schema = @Schema(implementation = UpdateTaskStatusRequest.class),
                            examples = @ExampleObject(
                                    name = "Update status example",
                                    value = """
                                    {
                                      "status": "IN_PROGRESS"
                                    }
                                    """
                            )
                    )
            )
            @Valid @RequestBody UpdateTaskStatusRequest request,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        TaskResponse data = taskService.updateStatus(authentication, actorId, id, request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }
}
