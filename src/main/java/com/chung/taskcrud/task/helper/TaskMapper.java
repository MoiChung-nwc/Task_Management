package com.chung.taskcrud.task.helper;

import com.chung.taskcrud.task.dto.response.TaskResponse;
import com.chung.taskcrud.task.entity.Tag;
import com.chung.taskcrud.task.entity.Task;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class TaskMapper {

    public TaskResponse toResponse(Task task) {
        Long createdById = task.getCreatedBy() != null ? task.getCreatedBy().getId() : null;
        String createdByEmail = task.getCreatedBy() != null ? task.getCreatedBy().getEmail() : null;
        Long assigneeId = task.getAssignee() != null ? task.getAssignee().getId() : null;
        String assigneeEmail = task.getAssignee() != null ? task.getAssignee().getEmail() : null;

        List<String> tags = task.getTags() == null ? List.of()
                : task.getTags().stream().map(Tag::getName).sorted().toList();

        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .createdById(createdById)
                .createdByEmail(createdByEmail)
                .assigneeId(assigneeId)
                .assigneeEmail(assigneeEmail)
                .tags(tags)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
