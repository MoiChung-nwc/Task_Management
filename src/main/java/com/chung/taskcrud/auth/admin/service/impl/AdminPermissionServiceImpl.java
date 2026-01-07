package com.chung.taskcrud.auth.admin.service.impl;

import com.chung.taskcrud.auth.admin.dto.request.*;
import com.chung.taskcrud.auth.admin.dto.response.PermissionResponse;
import com.chung.taskcrud.auth.admin.service.AdminPermissionService;
import com.chung.taskcrud.auth.entity.Permission;
import com.chung.taskcrud.auth.repository.PermissionRepository;
import com.chung.taskcrud.common.exception.AppException;
import com.chung.taskcrud.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminPermissionServiceImpl implements AdminPermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> listPermissions() {
        return permissionRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PermissionResponse createPermission(CreatePermissionRequest request) {
        String name = request.getName().trim().toUpperCase();

        permissionRepository.findByName(name).ifPresent(p -> {
            throw new AppException(ErrorCode.PERMISSION_ALREADY_EXISTS);
        });

        Permission p = Permission.builder()
                .name(name)
                .description(request.getDescription())
                .build();

        permissionRepository.save(p);
        return toResponse(p);
    }

    @Override
    public PermissionResponse updatePermission(Long permissionId, UpdatePermissionRequest request) {
        Permission p = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));

        // đổi name
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            String newName = request.getName().trim().toUpperCase();

            // nếu đổi sang name khác thì check duplicate
            if (!newName.equals(p.getName())) {
                permissionRepository.findByName(newName).ifPresent(exist -> {
                    throw new AppException(ErrorCode.PERMISSION_ALREADY_EXISTS, "Permission name already exists: " + newName);
                });
                p.setName(newName);
            }
        }

        // đổi description
        if (request.getDescription() != null) {
            p.setDescription(request.getDescription());
        }

        permissionRepository.save(p);
        return toResponse(p);
    }

    @Override
    public void deletePermission(Long permissionId) {
        Permission p = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));
        permissionRepository.delete(p);
    }

    private PermissionResponse toResponse(Permission p) {
        return PermissionResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .build();
    }
}
