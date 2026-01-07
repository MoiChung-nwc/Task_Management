package com.chung.taskcrud.auth.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMyProfileRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;
}