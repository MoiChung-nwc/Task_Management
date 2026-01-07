package com.chung.taskcrud.log.dto.response;

import com.chung.taskcrud.log.entity.TaskLogEventType;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskLogResponse {
    private Long id;
    private Long taskId;
    private TaskLogEventType eventType;
    private Long actorId;
    private String actorEmail;
    private Instant createdAt;
    private List<TaskLogChangeResponse> changes;
}
