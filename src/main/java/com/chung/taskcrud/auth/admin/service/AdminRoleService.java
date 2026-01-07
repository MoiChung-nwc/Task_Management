package com.chung.taskcrud.auth.admin.service;

import com.chung.taskcrud.auth.admin.dto.request.CreateRoleRequest;
import com.chung.taskcrud.auth.admin.dto.request.UpdateRoleRequest;
import com.chung.taskcrud.auth.admin.dto.response.RoleResponse;

import java.util.List;

public interface AdminRoleService {
    List<RoleResponse> listRoles();
    RoleResponse createRole(CreateRoleRequest request);
    RoleResponse updateRole(Long roleId, UpdateRoleRequest request);
    void deleteRole(Long roleId);
}
