package com.chung.taskcrud.auth.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeMyEmailRequest {
    @Email(message = "Email is invalid")
    @NotBlank(message = "Email is required")
    private String newEmail;
}
