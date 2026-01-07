package com.chung.taskcrud.auth.admin.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserSummaryResponse {
    private Long id;
    private String email;
    private String fullName;
    private boolean enabled;
    private List<String> roles;
}
