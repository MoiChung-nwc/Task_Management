package com.chung.taskcrud.auth.controller;

import com.chung.taskcrud.auth.dto.request.*;
import com.chung.taskcrud.auth.dto.response.AuthResponse;
import com.chung.taskcrud.auth.dto.response.LoginResponse;
import com.chung.taskcrud.auth.service.AuthService;
import com.chung.taskcrud.common.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private String traceId() {
        return UUID.randomUUID().toString();
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest http
    ) {
        authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(null, http.getRequestURI(), traceId()));
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verify(
            @RequestParam("token") String token,
            HttpServletRequest http
    ) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success("Verified successfully", http.getRequestURI(), traceId()));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest http
    ) {
        LoginResponse data = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshRequest request,
            HttpServletRequest http
    ) {
        AuthResponse data = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody LogoutRequest request,
            HttpServletRequest http
    ) {
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.success(null, http.getRequestURI(), traceId()));
    }
}