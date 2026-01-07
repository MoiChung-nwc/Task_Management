package com.chung.taskcrud.task.dto.response;

import com.chung.taskcrud.task.entity.TaskPriority;
import com.chung.taskcrud.task.entity.TaskStatus;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String description;

    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;

    private Long createdById;
    private String createdByEmail;

    private Long assigneeId;
    private String assigneeEmail;

    private List<String> tags;

    private Instant createdAt;
    private Instant updatedAt;
}
