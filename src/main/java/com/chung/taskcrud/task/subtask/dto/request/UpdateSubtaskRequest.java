package com.chung.taskcrud.task.subtask.dto.request;

import com.chung.taskcrud.task.subtask.entity.SubtaskStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSubtaskRequest {

    private String title;
    private SubtaskStatus status;
}
