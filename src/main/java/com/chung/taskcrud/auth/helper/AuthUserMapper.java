package com.chung.taskcrud.auth.helper;

import com.chung.taskcrud.auth.entity.Permission;
import com.chung.taskcrud.auth.entity.Role;
import com.chung.taskcrud.auth.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthUserMapper {

    public List<String> extractRoleNames(User user) {
        return user.getRoles().stream()
                .map(Role::getName)
                .distinct()
                .toList();
    }

    public List<String> extraPermissionNames(User user) {
        return user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(Permission::getName)
                .distinct()
                .toList();
    }
}
