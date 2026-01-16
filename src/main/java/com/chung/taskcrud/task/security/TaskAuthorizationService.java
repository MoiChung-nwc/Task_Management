package com.chung.taskcrud.task.security;

import com.chung.taskcrud.common.exception.AppException;
import com.chung.taskcrud.common.exception.ErrorCode;
import com.chung.taskcrud.task.entity.Task;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class TaskAuthorizationService {

    private static final String SYSTEM_ADMIN = "SYSTEM_ADMIN";
    private static final String TASK_READ = "TASK_READ";
    private static final String TASK_UPDATE_OWN_OR_ASSIGNED = "TASK_UPDATE_OWN_OR_ASSIGNED";
    private static final String TASK_DELETE_OWN_OR_ASSIGNED = "TASK_DELETE_OWN_OR_ASSIGNED";

    public boolean isSystemAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> SYSTEM_ADMIN.equals(a.getAuthority()));
    }

    private boolean has(Authentication auth, String authority) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> authority.equals(a.getAuthority()));
    }

    private void require(Authentication auth, String authority) {
        if (isSystemAdmin(auth)) return;
        if (!has(auth, authority)) {
            throw new AppException(ErrorCode.TASK_ACCESS_DENIED, "Missing permission: " + authority);
        }
    }

    private void requireOwnerOrAssignee(Authentication auth, Long actorId, Task task) {
        if (isSystemAdmin(auth)) return;

        Long createdById = task.getCreatedBy().getId();
        Long assigneeId = (task.getAssignee() != null) ? task.getAssignee().getId() : null;

        boolean allowed = createdById.equals(actorId) || (assigneeId != null && assigneeId.equals(actorId));
        if (!allowed) {
            throw new AppException(ErrorCode.TASK_ACCESS_DENIED,
                    "You can only access/modify tasks you created or tasks assigned to you");
        }
    }

    public void assertCanView(Authentication auth, Long actorId, Task task) {
        require(auth, TASK_READ);
        requireOwnerOrAssignee(auth, actorId, task);
    }

    public void assertCanModify(Authentication auth, Long actorId, Task task) {
        require(auth, TASK_UPDATE_OWN_OR_ASSIGNED);
        requireOwnerOrAssignee(auth, actorId, task);
    }

    public void assertCanDelete(Authentication auth, Long actorId, Task task) {
        require(auth, TASK_DELETE_OWN_OR_ASSIGNED);
        requireOwnerOrAssignee(auth, actorId, task);
    }
}
