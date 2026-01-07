package com.chung.taskcrud.task.dto.request;

import com.chung.taskcrud.task.entity.TaskPriority;
import com.chung.taskcrud.task.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTaskRequest {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private Long assigneeId;
    private List<String> tags;
}
