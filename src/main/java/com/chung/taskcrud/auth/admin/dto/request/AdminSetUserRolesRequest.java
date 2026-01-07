package com.chung.taskcrud.auth.admin.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSetUserRolesRequest {

    @NotEmpty(message = "roleName is required")
    private List<String> roleNames;
}
