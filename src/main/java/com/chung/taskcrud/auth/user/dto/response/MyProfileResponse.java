package com.chung.taskcrud.auth.user.dto.response;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyProfileResponse {
    private Long id;
    private String email;
    private String fullName;
    private boolean enabled;
    private Instant createdAt;
    private Instant updateAt;
}
