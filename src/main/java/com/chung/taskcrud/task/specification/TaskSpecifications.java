package com.chung.taskcrud.task.specification;

import com.chung.taskcrud.task.entity.Task;
import com.chung.taskcrud.task.entity.TaskPriority;
import com.chung.taskcrud.task.entity.TaskStatus;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TaskSpecifications {

    public static Specification<Task> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<Task> statusEquals(TaskStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Task> priorityEquals(TaskPriority priority) {
        return (root, query, cb) -> cb.equal(root.get("priority"), priority);
    }

    public static Specification<Task> dueBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from != null && to != null) return cb.between(root.get("dueDate"), from, to);
            if (from != null) return cb.greaterThanOrEqualTo(root.get("dueDate"), from);
            if (to != null) return cb.lessThanOrEqualTo(root.get("dueDate"), to);
            return cb.conjunction();
        };
    }

    public static Specification<Task> hasTagName(String tagName) {
        return (root, query, cb) -> {
            query.distinct(true);
            var join = root.join("tags", JoinType.LEFT);
            return cb.equal(join.get("name"), tagName);
        };
    }

    public static Specification<Task> assigneeIdEquals(Long assigneeId) {
        return (root, query, cb) -> cb.equal(root.get("assignee").get("id"), assigneeId);
    }

    // Visibility for normal user: createdBy == actor OR assignee == actor
    public static Specification<Task> visibleTo(Long actorId) {
        return (root, query, cb) -> cb.or(
                cb.equal(root.get("createdBy").get("id"), actorId),
                cb.equal(root.get("assignee").get("id"), actorId)
        );
    }
}