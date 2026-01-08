package com.chung.taskcrud.auth.admin.controller;

import com.chung.taskcrud.auth.admin.dto.request.CreateRoleRequest;
import com.chung.taskcrud.auth.admin.dto.request.UpdateRoleRequest;
import com.chung.taskcrud.auth.admin.dto.response.RoleResponse;
import com.chung.taskcrud.auth.admin.service.AdminRoleService;
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
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
@Tag(name = "Admin - Roles", description = "Quản trị roles (yêu cầu quyền SYSTEM_ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class AdminRoleController {

    private final AdminRoleService adminRoleService;

    private String traceId() {
        return UUID.randomUUID().toString();
    }

    @Operation(
            summary = "Danh sách roles",
            description = "Trả về danh sách toàn bộ roles. Chỉ SYSTEM_ADMIN được phép gọi."
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
    public ResponseEntity<ApiResponse<List<RoleResponse>>> list(HttpServletRequest http) {
        return ResponseEntity.ok(
                ApiResponse.success(adminRoleService.listRoles(), http.getRequestURI(), traceId())
        );
    }

    @Operation(
            summary = "Tạo role",
            description = "Tạo mới một role. Body yêu cầu theo CreateRoleRequest."
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
                    description = "Trùng name/code (nếu hệ thống có)",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponse>> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload tạo role",
                    content = @Content(
                            schema = @Schema(implementation = CreateRoleRequest.class),
                            examples = @ExampleObject(
                                    name = "Create role example",
                                    value = """
                                    {
                                      "name": "MANAGER",
                                      "description": "Manager role",
                                      "permissions": ["TASK_CREATE", "TASK_UPDATE", "TASK_VIEW"]
                                    }
                                    """
                            )
                    )
            )
            @Valid @RequestBody CreateRoleRequest request,
            HttpServletRequest http
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(adminRoleService.createRole(request), http.getRequestURI(), traceId())
        );
    }

    @Operation(
            summary = "Cập nhật role",
            description = "Cập nhật role theo ID. Body yêu cầu theo UpdateRoleRequest."
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
                    description = "Không tìm thấy role",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponse>> update(
            @Parameter(description = "Role ID", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload cập nhật role",
                    content = @Content(
                            schema = @Schema(implementation = UpdateRoleRequest.class),
                            examples = @ExampleObject(
                                    name = "Update role example",
                                    value = """
                                    {
                                      "description": "Manager role (updated)",
                                      "permissions": ["TASK_CREATE", "TASK_UPDATE", "TASK_VIEW", "TASK_DELETE"]
                                    }
                                    """
                            )
                    )
            )
            @Valid @RequestBody UpdateRoleRequest request,
            HttpServletRequest http
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(adminRoleService.updateRole(id, request), http.getRequestURI(), traceId())
        );
    }

    @Operation(
            summary = "Xóa role",
            description = "Xóa role theo ID."
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
                    description = "Không tìm thấy role",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "Role ID", example = "1")
            @PathVariable Long id,
            HttpServletRequest http
    ) {
        adminRoleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success(null, http.getRequestURI(), traceId()));
    }
}
