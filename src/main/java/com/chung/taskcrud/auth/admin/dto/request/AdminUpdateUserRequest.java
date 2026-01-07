package com.chung.taskcrud.auth.admin.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUpdateUserRequest {
    private String fullName;
    private Boolean enabled;
}
