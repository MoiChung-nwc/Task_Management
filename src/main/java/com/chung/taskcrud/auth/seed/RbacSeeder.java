package com.chung.taskcrud.auth.seed;

import com.chung.taskcrud.auth.entity.Permission;
import com.chung.taskcrud.auth.entity.Role;
import com.chung.taskcrud.auth.repository.PermissionRepository;
import com.chung.taskcrud.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class RbacSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public void run(String... args) {

        Permission systemAdmin = upsertPermission("SYSTEM_ADMIN", "Bypass all authorization checks");

        Permission taskRead   = upsertPermission("TASK_READ", "View tasks");
        Permission taskCreate = upsertPermission("TASK_CREATE", "Create tasks");

        Permission taskUpdateOwnOrAssigned = upsertPermission("TASK_UPDATE_OWN_OR_ASSIGNED", "Update own or assigned tasks");
        Permission taskDeleteOwnOrAssigned = upsertPermission("TASK_DELETE_OWN_OR_ASSIGNED", "Delete own or assigned tasks");

        Permission taskAssign = upsertPermission("TASK_ASSIGN", "Assign tasks to users");

        Permission adminGrant = upsertPermission("ADMIN_GRANT", "Grant/revoke admin role");

        upsertRole("USER", "Default user",
                Set.of(taskRead, taskCreate, taskUpdateOwnOrAssigned, taskDeleteOwnOrAssigned)
        );

        // ADMIN: SYSTEM_ADMIN => toàn quyền (bạn có thể thêm adminGrant/taskAssign cho rõ ràng)
        upsertRole("ADMIN", "System administrator",
                Set.of(systemAdmin, taskAssign, adminGrant, taskRead, taskCreate, taskUpdateOwnOrAssigned, taskDeleteOwnOrAssigned)
        );
    }

    private Permission upsertPermission(String name, String desc) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> permissionRepository.save(
                        Permission.builder().name(name).description(desc).build()
                ));
    }

    private Role upsertRole(String name, String desc, Set<Permission> perms) {
        Role role = roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(
                        Role.builder().name(name).description(desc).build()
                ));
        role.setDescription(desc);
        role.setPermissions(perms);
        return roleRepository.save(role);
    }
}
