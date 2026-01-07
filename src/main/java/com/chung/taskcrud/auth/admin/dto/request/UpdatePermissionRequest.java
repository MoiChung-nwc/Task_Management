package com.chung.taskcrud.auth.admin.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePermissionRequest {
    private String name;
    private String description;
}
