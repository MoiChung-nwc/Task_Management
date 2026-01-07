package com.chung.taskcrud.auth.user.service.impl;

import com.chung.taskcrud.auth.entity.EmailVerificationToken;
import com.chung.taskcrud.auth.entity.User;
import com.chung.taskcrud.auth.helper.AuthTokenHelper;
import com.chung.taskcrud.auth.repository.UserRepository;
import com.chung.taskcrud.auth.user.dto.request.ChangeMyEmailRequest;
import com.chung.taskcrud.auth.user.dto.request.ChangeMyPasswordRequest;
import com.chung.taskcrud.auth.user.dto.request.UpdateMyProfileRequest;
import com.chung.taskcrud.auth.user.dto.response.MyProfileResponse;
import com.chung.taskcrud.auth.user.service.MyAccountService;
import com.chung.taskcrud.common.exception.AppException;
import com.chung.taskcrud.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MyAccountServiceImpl implements MyAccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenHelper tokenHelper;

    @Override
    @Transactional(readOnly = true)
    public MyProfileResponse getMe(Long userId) {
        User user = getUserOrThrow(userId);
        return toProfile(user);
    }

    @Override
    public MyProfileResponse updateProfile(Long userId, UpdateMyProfileRequest request) {
        User user = getUserOrThrow(userId);

        String fullName = request.getFullName().trim();
        user.setFullName(fullName);

        userRepository.save(user);
        return toProfile(user);
    }

    @Override
    public void changePassword(Long userId, ChangeMyPasswordRequest request) {
        User user = getUserOrThrow(userId);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Current password is incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "New password must be different from current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

    }

    @Override
    public void changeEmail(Long userId, ChangeMyEmailRequest request) {
        User user = getUserOrThrow(userId);

        String newEmail = normalizeEmail(request.getNewEmail());
        String oldEmail = normalizeEmail(user.getEmail());

        if (newEmail.equals(oldEmail)) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "New email is same as current email");
        }

        if (userRepository.existsByEmail(newEmail)) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        user.setEmail(newEmail);
        user.setEnabled(false);
        userRepository.save(user);

        EmailVerificationToken vt = tokenHelper.createAndSaveVerifyToken(user);
        tokenHelper.sendVerifyEmail(newEmail, vt.getToken());
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "User not found"));
    }

    private MyProfileResponse toProfile(User user) {
        return MyProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .updateAt(user.getUpdatedAt())
                .build();
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}