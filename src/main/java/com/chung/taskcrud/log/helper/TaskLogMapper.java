package com.chung.taskcrud.log.helper;

import com.chung.taskcrud.log.dto.response.TaskLogChangeResponse;
import com.chung.taskcrud.log.dto.response.TaskLogResponse;
import com.chung.taskcrud.log.entity.TaskLog;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskLogMapper {

    public TaskLogResponse toResponse(TaskLog log) {
        Long actorId = (log.getActor() != null) ? log.getActor().getId() : null;
        String actorEmail = (log.getActor() != null) ? log.getActor().getEmail() : null;

        List<TaskLogChangeResponse> changes = log.getChanges().stream()
                .map(c -> TaskLogChangeResponse.builder()
                        .fielName(c.getFieldName())
                        .oldValue(c.getOldValue())
                        .newValue(c.getNewValue())
                        .build())
                .toList();

        return TaskLogResponse.builder()
                .id(log.getId())
                .taskId(log.getTask().getId())
                .taskTitle(log.getTask().getTitle())
                .eventType(log.getEventType())
                .actorId(actorId)
                .actorEmail(actorEmail)
                .createdAt(log.getCreatedAt())
                .changes(changes)
                .build();
    }

}
