package com.chung.taskcrud.task.helper;

import com.chung.taskcrud.task.entity.Task;
import com.chung.taskcrud.task.entity.TaskPriority;
import com.chung.taskcrud.task.entity.TaskStatus;
import com.chung.taskcrud.task.security.TaskAuthorizationService;
import com.chung.taskcrud.task.specification.TaskSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class TaskQueryHelper {

    private final TaskAuthorizationService authorizationService;

    public Specification<Task> buildSpec(
            Authentication auth,
            Long actorId,
            TaskStatus status,
            TaskPriority priority,
            LocalDate dueFrom,
            LocalDate dueTo,
            String tag,
            Long assigneeId
    ) {
        Specification<Task> spec = Specification.where(TaskSpecifications.notDeleted());

        if (!authorizationService.isSystemAdmin(auth)) {
            spec = spec.and(TaskSpecifications.visibleTo(actorId));
        }

        if (status != null) spec = spec.and(TaskSpecifications.statusEquals(status));
        if (priority != null) spec = spec.and(TaskSpecifications.priorityEquals(priority));
        if (dueFrom != null || dueTo != null) spec = spec.and(TaskSpecifications.dueBetween(dueFrom, dueTo));
        if (tag != null && !tag.trim().isEmpty()) spec = spec.and(TaskSpecifications.hasTagName(tag.trim()));
        if (assigneeId != null) spec = spec.and(TaskSpecifications.assigneeIdEquals(assigneeId));

        return spec;
    }

    /**
     * Optional: validate sort field to prevent bad request / SQL issues.
     * Bạn có thể whitelist field hợp lệ (createdAt, dueDate, priority,...)
     */
    public Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) return Sort.by(Sort.Direction.DESC, "createdAt");

        String[] parts = sort.split(",");
        String field = parts[0];
        Sort.Direction dir = (parts.length > 1 && "asc".equalsIgnoreCase(parts[1]))
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return Sort.by(dir, field);
    }
}