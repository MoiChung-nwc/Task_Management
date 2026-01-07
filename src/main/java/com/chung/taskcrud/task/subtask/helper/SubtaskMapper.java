package com.chung.taskcrud.task.subtask.helper;

import com.chung.taskcrud.task.subtask.dto.response.SubtaskResponse;
import com.chung.taskcrud.task.subtask.entity.Subtask;
import org.springframework.stereotype.Component;

@Component
public class SubtaskMapper {

    public SubtaskResponse toResponse(Subtask s) {
        return SubtaskResponse.builder()
                .id(s.getId())
                .taskId(s.getTask() != null ? s.getTask().getId() : null)
                .title(s.getTitle())
                .status(s.getStatus())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}
