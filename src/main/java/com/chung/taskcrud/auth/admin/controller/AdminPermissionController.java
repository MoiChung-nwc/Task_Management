package com.chung.taskcrud.auth.admin.controller;

import com.chung.taskcrud.auth.admin.dto.request.*;
import com.chung.taskcrud.auth.admin.dto.response.PermissionResponse;
import com.chung.taskcrud.auth.admin.service.AdminPermissionService;
import com.chung.taskcrud.common.dto.response.ApiResponse;
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
public class AdminPermissionController {

    private final AdminPermissionService adminPermissionService;

    private String traceId() {
        return UUID.randomUUID().toString();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> list(HttpServletRequest http) {
        return ResponseEntity.ok(ApiResponse.success(adminPermissionService.listPermissions(), http.getRequestURI(), traceId()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PermissionResponse>> create(@Valid @RequestBody CreatePermissionRequest request, HttpServletRequest http) {
        return ResponseEntity.ok(ApiResponse.success(adminPermissionService.createPermission(request), http.getRequestURI(), traceId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionResponse>> update(@PathVariable Long id, @Valid @RequestBody UpdatePermissionRequest request, HttpServletRequest http) {
        return ResponseEntity.ok(ApiResponse.success(adminPermissionService.updatePermission(id, request), http.getRequestURI(), traceId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, HttpServletRequest http) {
        adminPermissionService.deletePermission(id);
        return ResponseEntity.ok(ApiResponse.success(null, http.getRequestURI(), traceId()));
    }
}
