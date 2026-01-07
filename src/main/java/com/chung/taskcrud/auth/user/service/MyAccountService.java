package com.chung.taskcrud.auth.user.service;

import com.chung.taskcrud.auth.user.dto.request.ChangeMyEmailRequest;
import com.chung.taskcrud.auth.user.dto.request.ChangeMyPasswordRequest;
import com.chung.taskcrud.auth.user.dto.request.UpdateMyProfileRequest;
import com.chung.taskcrud.auth.user.dto.response.MyProfileResponse;

public interface MyAccountService {
    MyProfileResponse getMe(Long userId);

    MyProfileResponse updateProfile(Long userId, UpdateMyProfileRequest request);

    void changePassword(Long userId, ChangeMyPasswordRequest request);

    void changeEmail(Long userId, ChangeMyEmailRequest request);
}
