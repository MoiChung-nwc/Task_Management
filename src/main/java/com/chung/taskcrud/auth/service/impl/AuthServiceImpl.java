package com.chung.taskcrud.auth.service.impl;

import com.chung.taskcrud.auth.dto.request.*;
import com.chung.taskcrud.auth.dto.response.AuthResponse;
import com.chung.taskcrud.auth.dto.response.LoginResponse;
import com.chung.taskcrud.auth.entity.EmailVerificationToken;
import com.chung.taskcrud.auth.entity.RefreshToken;
import com.chung.taskcrud.auth.entity.Role;
import com.chung.taskcrud.auth.entity.User;
import com.chung.taskcrud.auth.helper.AuthTokenHelper;
import com.chung.taskcrud.auth.helper.AuthUserMapper;
import com.chung.taskcrud.auth.repository.RoleRepository;
import com.chung.taskcrud.auth.repository.UserRepository;
import com.chung.taskcrud.auth.service.AuthService;
import com.chung.taskcrud.auth.service.JwtService;
import com.chung.taskcrud.common.exception.AppException;
import com.chung.taskcrud.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String DEFAULT_ROLE = "USER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final AuthTokenHelper tokenHelper;
    private final AuthUserMapper userMapper;

    @Override
    public void register(RegisterRequest request) {
        String email = normalizeEmail(request.getEmail());

        if (userRepository.existsByEmail(email)) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        Role userRole = roleRepository.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> new AppException(ErrorCode.INTERNAL_SERVER_ERROR, "Missing role USER (seed not run?)"));

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .enabled(false)
                .build();
        user.getRoles().add(userRole);
        userRepository.save(user);

        EmailVerificationToken vt = tokenHelper.createAndSaveVerifyToken(user);
        tokenHelper.sendVerifyEmail(email, vt.getToken());
    }

    @Override
    public void verifyEmail(String token) {
        EmailVerificationToken vt = tokenHelper.getVerifyTokenOrThrow(token);
        tokenHelper.assertVerifyTokenUsable(vt);

        User user = vt.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        tokenHelper.markVerifyTokenUsed(vt);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String email = normalizeEmail(request.getEmail());

        authenticate(email, request.getPassword());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_INVALID_CREDENTIALS));

        if (!user.isEnabled()) {
            throw new AppException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = tokenHelper.issueRefreshToken(user);

        List<String> roles = userMapper.extractRoleNames(user);
        List<String> permissions = userMapper.extraPermissionNames(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .roles(roles)
                .permissions(permissions)
                .build();
    }

    @Override
    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken old = tokenHelper.getRefreshTokenOrThrow(request.getRefreshToken());
        tokenHelper.assertRefreshTokenUsable(old);

        User user = old.getUser();
        if (!user.isEnabled()) {
            throw new AppException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        // rotation
        tokenHelper.revokeRefreshToken(old);

        String newAccess = jwtService.generateAccessToken(user);
        String newRefresh = tokenHelper.issueRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(newAccess)
                .refreshToken(newRefresh)
                .build();
    }

    @Override
    public void logout(LogoutRequest request) {
        tokenHelper.getRefreshTokenOrThrow(request.getRefreshToken());

        RefreshToken rt = tokenHelper.getRefreshTokenOrThrow(request.getRefreshToken());
        tokenHelper.revokeIfNeeded(rt);
    }

    private void authenticate(String email, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (AuthenticationException e) {
            throw new AppException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }
    }

    private String normalizeEmail(String email) {
        if (email == null) return null;
        return email.trim().toLowerCase();
    }
}
