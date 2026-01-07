package com.chung.taskcrud.auth.user.controller;

import com.chung.taskcrud.auth.user.dto.request.ChangeMyEmailRequest;
import com.chung.taskcrud.auth.user.dto.request.ChangeMyPasswordRequest;
import com.chung.taskcrud.auth.user.dto.request.UpdateMyProfileRequest;
import com.chung.taskcrud.auth.user.dto.response.MyProfileResponse;
import com.chung.taskcrud.auth.user.dto.response.SimpleMessageResponse;
import com.chung.taskcrud.auth.user.service.MyAccountService;
import com.chung.taskcrud.common.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class MyAccountController {

    private final MyAccountService myAccountService;

    private String traceId() {
        return UUID.randomUUID().toString();
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<MyProfileResponse>> me(Authentication authentication, HttpServletRequest http) {
        Long userId = (Long) authentication.getPrincipal();
        MyProfileResponse data = myAccountService.getMe(userId);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<MyProfileResponse>> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateMyProfileRequest request,
            HttpServletRequest http
            ) {
        Long userId = (Long) authentication.getPrincipal();
        MyProfileResponse data = myAccountService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @PutMapping("/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<SimpleMessageResponse>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangeMyPasswordRequest request,
            HttpServletRequest http
            ) {
        Long userId = (Long) authentication.getPrincipal();
        myAccountService.changePassword(userId, request);

        return ResponseEntity.ok(ApiResponse.success(
                SimpleMessageResponse.builder().message("Password updated successfully").build(),
                http.getRequestURI(),
                traceId()
        ));
    }

    @PutMapping("/email")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<SimpleMessageResponse>> changeEmail(
            Authentication authentication,
            @Valid @RequestBody ChangeMyEmailRequest request,
            HttpServletRequest http
    ) {
        Long userId = (Long) authentication.getPrincipal();
        myAccountService.changeEmail(userId, request);

        return ResponseEntity.ok(ApiResponse.success(
                SimpleMessageResponse.builder().message("Email updated. Please verify your new email to login.").build(),
                http.getRequestURI(),
                traceId()
        ));
    }
}
