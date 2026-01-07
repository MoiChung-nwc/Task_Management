package com.chung.taskcrud.task.dto.response;

import com.chung.taskcrud.log.dto.response.TaskLogResponse;
import com.chung.taskcrud.task.comment.dto.response.CommentResponse;
import com.chung.taskcrud.task.subtask.dto.response.SubtaskResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDetailResponse {

    private TaskResponse task;

    @Builder.Default
    private List<SubtaskResponse> subtasks = List.of();

    @Builder.Default
    private List<CommentResponse> comments = List.of();

    @Builder.Default
    private List<TaskLogResponse> logs = List.of();
}
