package com.chung.taskcrud.task.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignTaskRequest {
    @NotNull(message = "AssigneeId is required")
    private Long assigneeId;
}
