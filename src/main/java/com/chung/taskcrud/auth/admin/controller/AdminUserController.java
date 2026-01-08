package com.chung.taskcrud.auth.admin.controller;

import com.chung.taskcrud.auth.admin.dto.request.AdminCreateUserRequest;
import com.chung.taskcrud.auth.admin.dto.request.AdminSetUserRolesRequest;
import com.chung.taskcrud.auth.admin.dto.request.AdminUpdateUserRequest;
import com.chung.taskcrud.auth.admin.dto.response.AdminUserDetailResponse;
import com.chung.taskcrud.auth.admin.dto.response.AdminUserSummaryResponse;
import com.chung.taskcrud.auth.admin.service.AdminUserService;
import com.chung.taskcrud.common.dto.response.ApiResponse;
import com.chung.taskcrud.common.dto.response.PageResponse;
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
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
@Tag(name = "Admin - Users", description = "Quản trị người dùng (yêu cầu quyền SYSTEM_ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class AdminUserController {

    private final AdminUserService adminUserService;

    private String traceId() {
        return UUID.randomUUID().toString();
    }

    @Operation(
            summary = "Danh sách users (phân trang)",
            description = "Trả về danh sách người dùng theo phân trang. Chỉ SYSTEM_ADMIN được phép gọi."
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
    public ResponseEntity<ApiResponse<PageResponse<AdminUserSummaryResponse>>> list(
            @Parameter(description = "Trang (bắt đầu từ 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Kích thước trang", example = "20")
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest http
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        var data = adminUserService.listUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @Operation(
            summary = "Chi tiết user theo id",
            description = "Lấy thông tin chi tiết một người dùng theo ID. Chỉ SYSTEM_ADMIN."
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy user",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminUserDetailResponse>> detail(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long id,
            HttpServletRequest http
    ) {
        var data = adminUserService.getUser(id);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @Operation(
            summary = "Tạo user mới",
            description = "Tạo mới một người dùng. Body yêu cầu theo AdminCreateUserRequest."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Tạo thành công (API đang trả 200)",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "Success example (shape depends on your ApiResponse)",
                                    value = """
                                    {
                                      "success": true,
                                      "message": "OK",
                                      "path": "/api/admin/users",
                                      "traceId": "3f8b7c77-7d4c-4c7c-9fb0-6e6a1d9b6c2e",
                                      "data": { }
                                    }
                                    """
                            )
                    )
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
                    description = "Trùng email/username (nếu hệ thống có)",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<AdminUserDetailResponse>> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload tạo user",
                    content = @Content(
                            schema = @Schema(implementation = AdminCreateUserRequest.class),
                            examples = @ExampleObject(
                                    name = "Create user example",
                                    value = """
                                    {
                                      "email": "admin1@example.com",
                                      "username": "admin1",
                                      "password": "P@ssw0rd123",
                                      "fullName": "Admin One",
                                      "enabled": true,
                                      "roles": ["SYSTEM_ADMIN"]
                                    }
                                    """
                            )
                    )
            )
            @Valid @RequestBody AdminCreateUserRequest request,
            HttpServletRequest http
    ) {
        var data = adminUserService.createUser(request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @Operation(
            summary = "Cập nhật user",
            description = "Cập nhật thông tin người dùng theo ID. Body theo AdminUpdateUserRequest."
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
                    description = "Không tìm thấy user",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminUserDetailResponse>> update(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload cập nhật user",
                    content = @Content(
                            schema = @Schema(implementation = AdminUpdateUserRequest.class),
                            examples = @ExampleObject(
                                    name = "Update user example",
                                    value = """
                                    {
                                      "fullName": "Admin One Updated",
                                      "enabled": true
                                    }
                                    """
                            )
                    )
            )
            @Valid @RequestBody AdminUpdateUserRequest request,
            HttpServletRequest http
    ) {
        var data = adminUserService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @Operation(
            summary = "Xóa user",
            description = "Xóa người dùng theo ID."
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
                    description = "Không tìm thấy user",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long id,
            HttpServletRequest http
    ) {
        adminUserService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, http.getRequestURI(), traceId()));
    }

    @Operation(
            summary = "Gán roles cho user",
            description = "Set lại danh sách roles cho user theo ID. Body theo AdminSetUserRolesRequest."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Gán roles thành công",
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
                    description = "Không tìm thấy user",
                    content = @Content
            )
    })
    @PutMapping("/{id}/roles")
    public ResponseEntity<ApiResponse<AdminUserDetailResponse>> setRoles(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload set roles",
                    content = @Content(
                            schema = @Schema(implementation = AdminSetUserRolesRequest.class),
                            examples = @ExampleObject(
                                    name = "Set roles example",
                                    value = """
                                    {
                                      "roles": ["SYSTEM_ADMIN", "MANAGER"]
                                    }
                                    """
                            )
                    )
            )
            @Valid @RequestBody AdminSetUserRolesRequest request,
            HttpServletRequest http
    ) {
        var data = adminUserService.setUserRoles(id, request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }
}
