package com.chung.taskcrud.auth.admin.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRoleRequest {
    private String description;
    private List<String> permissionNames;
}
