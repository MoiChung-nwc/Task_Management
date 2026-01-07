package com.chung.taskcrud.auth.service;

import com.chung.taskcrud.auth.dto.request.*;
import com.chung.taskcrud.auth.dto.response.AuthResponse;
import com.chung.taskcrud.auth.dto.response.LoginResponse;

public interface AuthService {

    void register(RegisterRequest request);

    void verifyEmail(String token);

    LoginResponse login(LoginRequest request);

    AuthResponse refresh(RefreshRequest request);

    void logout(LogoutRequest request);
}
