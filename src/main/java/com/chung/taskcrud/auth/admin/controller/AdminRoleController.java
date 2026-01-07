package com.chung.taskcrud.auth.admin.controller;

import com.chung.taskcrud.auth.admin.dto.request.*;
import com.chung.taskcrud.auth.admin.dto.response.RoleResponse;
import com.chung.taskcrud.auth.admin.service.AdminRoleService;
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
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
public class AdminRoleController {

    private final AdminRoleService adminRoleService;

    private String traceId() {
        return UUID.randomUUID().toString();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponse>>> list(HttpServletRequest http) {
        return ResponseEntity.ok(ApiResponse.success(adminRoleService.listRoles(), http.getRequestURI(), traceId()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponse>> create(@Valid @RequestBody CreateRoleRequest request, HttpServletRequest http) {
        return ResponseEntity.ok(ApiResponse.success(adminRoleService.createRole(request), http.getRequestURI(), traceId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponse>> update(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request, HttpServletRequest http) {
        return ResponseEntity.ok(ApiResponse.success(adminRoleService.updateRole(id, request), http.getRequestURI(), traceId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, HttpServletRequest http) {
        adminRoleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success(null, http.getRequestURI(), traceId()));
    }
}
