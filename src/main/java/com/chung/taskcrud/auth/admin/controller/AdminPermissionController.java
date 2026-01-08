package com.chung.taskcrud.auth.admin.controller;

import com.chung.taskcrud.auth.admin.dto.request.CreatePermissionRequest;
import com.chung.taskcrud.auth.admin.dto.request.UpdatePermissionRequest;
import com.chung.taskcrud.auth.admin.dto.response.PermissionResponse;
import com.chung.taskcrud.auth.admin.service.AdminPermissionService;
import com.chung.taskcrud.common.dto.response.ApiResponse;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/permissions")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
@Tag(name = "Admin - Permissions", description = "Quản trị permissions (yêu cầu quyền SYSTEM_ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class AdminPermissionController {

    private final AdminPermissionService adminPermissionService;

    private String traceId() {
        return UUID.randomUUID().toString();
    }

    @Operation(
            summary = "Danh sách permissions",
            description = "Trả về danh sách toàn bộ permissions. Chỉ SYSTEM_ADMIN được phép gọi."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Thành công",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập / thiếu token",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Không có quyền SYSTEM_ADMIN",
                    content = @Content
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> list(HttpServletRequest http) {
        return ResponseEntity.ok(
                ApiResponse.success(adminPermissionService.listPermissions(), http.getRequestURI(), traceId())
        );
    }

    @Operation(
            summary = "Tạo permission",
            description = "Tạo mới một permission. Body yêu cầu theo CreatePermissionRequest."
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
                    description = "Chưa đăng nhập / thiếu token",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Không có quyền SYSTEM_ADMIN",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Trùng code/name (nếu hệ thống có)",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<PermissionResponse>> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload tạo permission",
                    content = @Content(
                            schema = @Schema(implementation = CreatePermissionRequest.class),
                            examples = @ExampleObject(
                                    name = "Create permission example",
                                    value = """
                                    {
                                      "code": "TASK_CREATE",
                                      "name": "Create task",
                                      "description": "Allow creating tasks"
                                    }
                                    """
                            )
                    )
            )
            @Valid @RequestBody CreatePermissionRequest request,
            HttpServletRequest http
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(adminPermissionService.createPermission(request), http.getRequestURI(), traceId())
        );
    }

    @Operation(
            summary = "Cập nhật permission",
            description = "Cập nhật permission theo ID. Body yêu cầu theo UpdatePermissionRequest."
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
                    description = "Chưa đăng nhập / thiếu token",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Không có quyền SYSTEM_ADMIN",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy permission",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionResponse>> update(
            @Parameter(description = "Permission ID", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload cập nhật permission",
                    content = @Content(
                            schema = @Schema(implementation = UpdatePermissionRequest.class),
                            examples = @ExampleObject(
                                    name = "Update permission example",
                                    value = """
                                    {
                                      "name": "Create task (updated)",
                                      "description": "Allow creating tasks (updated)"
                                    }
                                    """
                            )
                    )
            )
            @Valid @RequestBody UpdatePermissionRequest request,
            HttpServletRequest http
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(adminPermissionService.updatePermission(id, request), http.getRequestURI(), traceId())
        );
    }

    @Operation(
            summary = "Xóa permission",
            description = "Xóa permission theo ID."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Xóa thành công (API đang trả 200)",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập / thiếu token",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Không có quyền SYSTEM_ADMIN",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy permission",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "Permission ID", example = "1")
            @PathVariable Long id,
            HttpServletRequest http
    ) {
        adminPermissionService.deletePermission(id);
        return ResponseEntity.ok(ApiResponse.success(null, http.getRequestURI(), traceId()));
    }
}
