package com.chung.taskcrud.auth.admin.controller;

import com.chung.taskcrud.auth.admin.dto.request.*;
import com.chung.taskcrud.auth.admin.dto.response.*;
import com.chung.taskcrud.auth.admin.service.AdminUserService;
import com.chung.taskcrud.common.dto.response.ApiResponse;
import com.chung.taskcrud.common.dto.response.PageResponse;
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
public class AdminUserController {

    private final AdminUserService adminUserService;

    private String traceId() {
        return UUID.randomUUID().toString();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AdminUserSummaryResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest http
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        var data = adminUserService.listUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminUserDetailResponse>> detail(
            @PathVariable Long id,
            HttpServletRequest http
    ) {
        var data = adminUserService.getUser(id);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdminUserDetailResponse>> create(
            @Valid @RequestBody AdminCreateUserRequest request,
            HttpServletRequest http
    ) {
        var data = adminUserService.createUser(request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminUserDetailResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody AdminUpdateUserRequest request,
            HttpServletRequest http
    ) {
        var data = adminUserService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            HttpServletRequest http
    ) {
        adminUserService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, http.getRequestURI(), traceId()));
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<ApiResponse<AdminUserDetailResponse>> setRoles(
            @PathVariable Long id,
            @Valid @RequestBody AdminSetUserRolesRequest request,
            HttpServletRequest http
    ) {
        var data = adminUserService.setUserRoles(id, request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }
}
