package com.chung.taskcrud.task.subtask.dto.response;

import com.chung.taskcrud.task.subtask.entity.SubtaskStatus;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubtaskResponse {

    private Long id;
    private Long taskId;

    private String title;
    private SubtaskStatus status;

    private Instant createdAt;
    private Instant updatedAt;
}
