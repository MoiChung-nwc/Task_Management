package com.chung.taskcrud.auth.admin.service.impl;

import com.chung.taskcrud.auth.admin.dto.request.*;
import com.chung.taskcrud.auth.admin.dto.response.RoleResponse;
import com.chung.taskcrud.auth.admin.service.AdminRoleService;
import com.chung.taskcrud.auth.entity.Permission;
import com.chung.taskcrud.auth.entity.Role;
import com.chung.taskcrud.auth.repository.PermissionRepository;
import com.chung.taskcrud.auth.repository.RoleRepository;
import com.chung.taskcrud.common.exception.AppException;
import com.chung.taskcrud.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminRoleServiceImpl implements AdminRoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> listRoles() {
        return roleRepository.findAll().stream()
                .map(this::toRoleResponse)
                .toList();
    }

    @Override
    public RoleResponse createRole(CreateRoleRequest request) {
        String name = request.getName().trim().toUpperCase();

        roleRepository.findByName(name).ifPresent(r -> {
            throw new AppException(ErrorCode.ROLE_ALREADY_EXISTS);
        });

        Role role = Role.builder()
                .name(name)
                .description(request.getDescription())
                .build();

        if (request.getPermissionNames() != null && !request.getPermissionNames().isEmpty()) {
            role.setPermissions(resolvePermissionsOrThrow(request.getPermissionNames()));
        }

        roleRepository.save(role);
        return toRoleResponse(role);
    }

    @Override
    public RoleResponse updateRole(Long roleId, UpdateRoleRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        if (request.getDescription() != null) role.setDescription(request.getDescription());

        if (request.getPermissionNames() != null) {
            role.setPermissions(resolvePermissionsOrThrow(request.getPermissionNames()));
        }

        roleRepository.save(role);
        return toRoleResponse(role);
    }

    @Override
    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        roleRepository.delete(role);
    }

    private Set<Permission> resolvePermissionsOrThrow(List<String> permissionNames) {
        Set<Permission> perms = new HashSet<>();
        for (String pn : permissionNames) {
            Permission p = permissionRepository.findByName(pn.trim().toUpperCase())
                    .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND, "Permission not found: " + pn));
            perms.add(p);
        }
        return perms;
    }

    private RoleResponse toRoleResponse(Role role) {
        List<String> perms = role.getPermissions().stream()
                .map(Permission::getName)
                .distinct()
                .toList();

        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(perms)
                .build();
    }
}
