package com.chung.taskcrud.auth.admin.service;

import com.chung.taskcrud.auth.admin.dto.request.CreatePermissionRequest;
import com.chung.taskcrud.auth.admin.dto.request.UpdatePermissionRequest;
import com.chung.taskcrud.auth.admin.dto.response.PermissionResponse;

import java.util.List;

public interface AdminPermissionService {
    List<PermissionResponse> listPermissions();
    PermissionResponse createPermission(CreatePermissionRequest request);
    PermissionResponse updatePermission(Long permissionId, UpdatePermissionRequest request);
    void deletePermission(Long permissionId);
}
