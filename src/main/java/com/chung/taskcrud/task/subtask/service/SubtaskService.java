package com.chung.taskcrud.task.subtask.service;

import com.chung.taskcrud.task.subtask.dto.request.CreateSubtaskRequest;
import com.chung.taskcrud.task.subtask.dto.request.UpdateSubtaskRequest;
import com.chung.taskcrud.task.subtask.dto.response.SubtaskResponse;
import org.springframework.security.core.Authentication;

public interface SubtaskService {

    SubtaskResponse create(Authentication auth, Long actorId, Long taskId, CreateSubtaskRequest request);

    SubtaskResponse update(Authentication auth, Long actorId, Long taskId, Long subtaskId, UpdateSubtaskRequest request);

    void delete(Authentication auth, Long actorId, Long taskId, Long subtaskId);

    SubtaskResponse detail(Authentication auth, Long actorId, Long taskId, Long subtaskId);
}
