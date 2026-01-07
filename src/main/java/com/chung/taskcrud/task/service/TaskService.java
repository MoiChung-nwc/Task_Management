package com.chung.taskcrud.task.service;

import com.chung.taskcrud.common.dto.response.PageResponse;
import com.chung.taskcrud.task.dto.request.AssignTaskRequest;
import com.chung.taskcrud.task.dto.request.CreateTaskRequest;
import com.chung.taskcrud.task.dto.request.UpdateTaskRequest;
import com.chung.taskcrud.task.dto.request.UpdateTaskStatusRequest;
import com.chung.taskcrud.task.dto.response.TaskDetailResponse;
import com.chung.taskcrud.task.dto.response.TaskResponse;
import com.chung.taskcrud.task.entity.TaskPriority;
import com.chung.taskcrud.task.entity.TaskStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;

public interface TaskService {
    TaskResponse create(Authentication auth, Long actorId, CreateTaskRequest request);

    TaskResponse update(Authentication auth, Long actorId, Long taskId, UpdateTaskRequest request);

    void softDelete(Authentication auth, Long actorId, Long taskId);

    PageResponse<TaskResponse> list(
            Authentication auth,
            Long actorId,
            TaskStatus status,
            TaskPriority priority,
            LocalDate dueFrom,
            LocalDate dueTo,
            String tag,
            Long assigneeId,
            Pageable pageable
    );

    TaskDetailResponse detail(Authentication auth, Long actorId, Long taskId);

    TaskResponse assign(Authentication auth, Long actorId, Long taskId, AssignTaskRequest request);

    TaskResponse updateStatus(Authentication auth, Long actorId, Long taskId, UpdateTaskStatusRequest request);
}
