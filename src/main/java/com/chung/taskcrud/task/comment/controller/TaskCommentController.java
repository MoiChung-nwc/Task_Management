package com.chung.taskcrud.task.comment.controller;

import com.chung.taskcrud.common.dto.response.ApiResponse;
import com.chung.taskcrud.common.dto.response.PageResponse;
import com.chung.taskcrud.task.comment.dto.request.CreateCommentRequest;
import com.chung.taskcrud.task.comment.dto.request.UpdateCommentRequest;
import com.chung.taskcrud.task.comment.dto.response.CommentResponse;
import com.chung.taskcrud.task.comment.service.TaskCommentService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks/{taskId}/comments")
@Tag(name = "Tasks - Comments", description = "APIs quản lý comment theo task")
@SecurityRequirement(name = "bearerAuth")
public class TaskCommentController {

    private final TaskCommentService commentService;

    private String traceId() {
        return UUID.randomUUID().toString();
    }

    @Operation(
            summary = "Tạo comment cho task",
            description = "Tạo mới comment cho một task theo taskId. Yêu cầu Bearer token."
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
    public ResponseEntity<ApiResponse<CommentResponse>> create(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Task ID", example = "100")
            @PathVariable Long taskId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload tạo comment",
                    content = @Content(
                            schema = @Schema(implementation = CreateCommentRequest.class),
                            examples = @ExampleObject(
                                    name = "Create comment example",
                                    value = """
                                    {
                                      "content": "Mình đã xử lý xong phần API, nhờ bạn review giúp."
                                    }
                                    """
                            )
                    )
            )
            @Valid @RequestBody CreateCommentRequest request,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        CommentResponse data = commentService.create(authentication, actorId, taskId, request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @Operation(
            summary = "Danh sách comment của task (phân trang)",
            description = "Lấy danh sách comment của một task theo taskId, có phân trang."
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
    public ResponseEntity<ApiResponse<PageResponse<CommentResponse>>> list(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Task ID", example = "100")
            @PathVariable Long taskId,
            @Parameter(description = "Trang (bắt đầu từ 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Kích thước trang", example = "10")
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);

        PageResponse<CommentResponse> data = commentService.list(authentication, actorId, taskId, pageable);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @Operation(
            summary = "Chi tiết comment",
            description = "Lấy chi tiết một comment theo taskId và commentId."
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
                    description = "Không tìm thấy task/comment",
                    content = @Content
            )
    })
    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> detail(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Task ID", example = "100")
            @PathVariable Long taskId,
            @Parameter(description = "Comment ID", example = "999")
            @PathVariable Long commentId,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        CommentResponse data = commentService.detail(authentication, actorId, taskId, commentId);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @Operation(
            summary = "Cập nhật comment",
            description = "Cập nhật nội dung comment theo taskId và commentId. Thường chỉ chủ comment hoặc role phù hợp mới được sửa (tuỳ logic)."
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
                    description = "Không có quyền sửa comment (tuỳ logic)",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy task/comment",
                    content = @Content
            )
    })
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> update(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Task ID", example = "100")
            @PathVariable Long taskId,
            @Parameter(description = "Comment ID", example = "999")
            @PathVariable Long commentId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload cập nhật comment",
                    content = @Content(
                            schema = @Schema(implementation = UpdateCommentRequest.class),
                            examples = @ExampleObject(
                                    name = "Update comment example",
                                    value = """
                                    {
                                      "content": "Mình đã update lại theo feedback, bạn xem giúp nhé."
                                    }
                                    """
                            )
                    )
            )
            @Valid @RequestBody UpdateCommentRequest request,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        CommentResponse data = commentService.update(authentication, actorId, taskId, commentId, request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @Operation(
            summary = "Xóa comment",
            description = "Xóa comment theo taskId và commentId. Thường chỉ chủ comment hoặc role phù hợp mới được xóa (tuỳ logic)."
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
                    description = "Không có quyền xóa comment (tuỳ logic)",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy task/comment",
                    content = @Content
            )
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Task ID", example = "100")
            @PathVariable Long taskId,
            @Parameter(description = "Comment ID", example = "999")
            @PathVariable Long commentId,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        commentService.delete(authentication, actorId, taskId, commentId);
        return ResponseEntity.ok(ApiResponse.success(null, http.getRequestURI(), traceId()));
    }
}
