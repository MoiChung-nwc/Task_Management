package com.chung.taskcrud.auth.admin.service.impl;

import com.chung.taskcrud.auth.admin.dto.request.*;
import com.chung.taskcrud.auth.admin.dto.response.*;
import com.chung.taskcrud.auth.admin.service.AdminUserService;
import com.chung.taskcrud.auth.entity.Permission;
import com.chung.taskcrud.auth.entity.Role;
import com.chung.taskcrud.auth.entity.User;
import com.chung.taskcrud.auth.repository.RoleRepository;
import com.chung.taskcrud.auth.repository.UserRepository;
import com.chung.taskcrud.common.dto.response.PageResponse;
import com.chung.taskcrud.common.exception.AppException;
import com.chung.taskcrud.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminUserSummaryResponse> listUsers(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);

        List<AdminUserSummaryResponse> items = page.getContent().stream()
                .map(this::toUserSummary)
                .toList();

        return PageResponse.<AdminUserSummaryResponse>builder()
                .items(items)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserDetailResponse getUser(Long userId) {
        User user = getUserOrThrow(userId);
        return toUserDetail(user);
    }

    @Override
    public AdminUserDetailResponse createUser(AdminCreateUserRequest request) {
        String email = normalizeEmail(request.getEmail());

        if (userRepository.existsByEmail(email)) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        Set<Role> roles = resolveRolesOrDefault(request.getRoleNames());

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .enabled(request.isEnabled())
                .roles(roles)
                .build();

        userRepository.save(user);
        return toUserDetail(user);
    }

    @Override
    public AdminUserDetailResponse updateUser(Long userId, AdminUpdateUserRequest request) {
        User user = getUserOrThrow(userId);

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getEnabled() != null) user.setEnabled(request.getEnabled());

        userRepository.save(user);
        return toUserDetail(user);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = getUserOrThrow(userId);
        // “xóa” theo hướng an toàn: disable tài khoản
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    public AdminUserDetailResponse setUserRoles(Long userId, AdminSetUserRolesRequest request) {
        User user = getUserOrThrow(userId);

        Set<Role> roles = resolveRolesOrThrow(request.getRoleNames());
        user.setRoles(roles);

        userRepository.save(user);
        return toUserDetail(user);
    }

    // ===== mapping / helper =====

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private Set<Role> resolveRolesOrDefault(List<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return resolveRolesOrThrow(List.of("USER"));
        }
        return resolveRolesOrThrow(roleNames);
    }

    private Set<Role> resolveRolesOrThrow(List<String> roleNames) {
        Set<Role> roles = new HashSet<>();
        for (String rn : roleNames) {
            Role role = roleRepository.findByName(rn.trim().toUpperCase())
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND, "Role not found: " + rn));
            roles.add(role);
        }
        return roles;
    }

    private AdminUserSummaryResponse toUserSummary(User user) {
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .distinct()
                .toList();

        return AdminUserSummaryResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .enabled(user.isEnabled())
                .roles(roles)
                .build();
    }

    private AdminUserDetailResponse toUserDetail(User user) {
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .distinct()
                .toList();

        List<String> permissions = user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(Permission::getName)
                .distinct()
                .toList();

        return AdminUserDetailResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .roles(roles)
                .permissions(permissions)
                .build();
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
