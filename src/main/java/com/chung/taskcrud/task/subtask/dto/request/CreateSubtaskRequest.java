package com.chung.taskcrud.task.subtask.dto.request;

import com.chung.taskcrud.task.subtask.entity.SubtaskStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSubtaskRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private SubtaskStatus status;
}
