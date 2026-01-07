package com.chung.taskcrud.task.dto.request;

import com.chung.taskcrud.task.entity.TaskPriority;
import com.chung.taskcrud.task.entity.TaskStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTaskRequest {
    private String title;
    private String description;

    private TaskStatus status;
    private TaskPriority priority;

    private LocalDate dueDate;

    private Long assigneeId;
    private List<String> tags;
}
