package com.chung.taskcrud.auth.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String accessToken;
    private String refreshToken;

    private Long userId;
    private String email;

    private List<String> roles;
    private List<String> permissions;
}
