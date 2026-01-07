package com.chung.taskcrud.auth.admin.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserDetailResponse {
    private Long id;
    private String email;
    private String fullName;
    private boolean enabled;
    private Instant createdAt;
    private Instant updatedAt;
    private List<String> roles;
    private List<String> permissions;
}
