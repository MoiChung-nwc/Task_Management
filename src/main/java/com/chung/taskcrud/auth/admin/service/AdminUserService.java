package com.chung.taskcrud.auth.admin.service;

import com.chung.taskcrud.auth.admin.dto.request.AdminCreateUserRequest;
import com.chung.taskcrud.auth.admin.dto.request.AdminSetUserRolesRequest;
import com.chung.taskcrud.auth.admin.dto.request.AdminUpdateUserRequest;
import com.chung.taskcrud.auth.admin.dto.response.AdminUserDetailResponse;
import com.chung.taskcrud.auth.admin.dto.response.AdminUserSummaryResponse;
import com.chung.taskcrud.common.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface AdminUserService {

    PageResponse<AdminUserSummaryResponse> listUsers(Pageable pageable);

    AdminUserDetailResponse getUser(Long userId);

    AdminUserDetailResponse createUser(AdminCreateUserRequest request);

    AdminUserDetailResponse updateUser(Long userId, AdminUpdateUserRequest request);

    void deleteUser(Long userId);

    AdminUserDetailResponse setUserRoles(Long userId, AdminSetUserRolesRequest request);
}
