package com.chung.taskcrud.task.subtask.service.impl;

import com.chung.taskcrud.common.exception.AppException;
import com.chung.taskcrud.common.exception.ErrorCode;
import com.chung.taskcrud.task.entity.Task;
import com.chung.taskcrud.task.repository.TaskRepository;
import com.chung.taskcrud.task.security.TaskAuthorizationService;
import com.chung.taskcrud.task.subtask.dto.request.CreateSubtaskRequest;
import com.chung.taskcrud.task.subtask.dto.request.UpdateSubtaskRequest;
import com.chung.taskcrud.task.subtask.dto.response.SubtaskResponse;
import com.chung.taskcrud.task.subtask.entity.Subtask;
import com.chung.taskcrud.task.subtask.entity.SubtaskStatus;
import com.chung.taskcrud.task.subtask.helper.SubtaskMapper;
import com.chung.taskcrud.task.subtask.repository.SubtaskRepository;
import com.chung.taskcrud.task.subtask.service.SubtaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class SubtaskServiceImpl implements SubtaskService {

    private final TaskRepository taskRepository;
    private final SubtaskRepository subtaskRepository;

    private final TaskAuthorizationService authorizationService;
    private final SubtaskMapper mapper;

    @Override
    public SubtaskResponse create(Authentication auth, Long actorId, Long taskId, CreateSubtaskRequest request) {
        Task task = getTaskOrThrow(taskId);
        authorizationService.assertCanModify(auth, actorId, task);

        Subtask subtask = Subtask.builder()
                .task(task)
                .title(request.getTitle().trim())
                .status(request.getStatus() != null ? request.getStatus() : SubtaskStatus.TODO)
                .build();

        subtaskRepository.save(subtask);
        return mapper.toResponse(subtask);
    }

    @Override
    public SubtaskResponse update(Authentication auth, Long actorId, Long taskId, Long subtaskId, UpdateSubtaskRequest request) {
        Task task = getTaskOrThrow(taskId);
        authorizationService.assertCanModify(auth, actorId, task);

        Subtask subtask = getSubtaskOrThrow(subtaskId);
        assertBelongsToTask(subtask, taskId);

        if (request.getTitle() != null) subtask.setTitle(request.getTitle().trim());
        if (request.getStatus() != null) subtask.setStatus(request.getStatus());

        subtaskRepository.save(subtask);
        return mapper.toResponse(subtask);
    }

    @Override
    public void delete(Authentication auth, Long actorId, Long taskId, Long subtaskId) {
        Task task = getTaskOrThrow(taskId);
        authorizationService.assertCanDelete(auth, actorId, task);

        Subtask subtask = getSubtaskOrThrow(subtaskId);
        assertBelongsToTask(subtask, taskId);

        if (!subtask.isDeleted()) {
            subtask.setDeletedAt(Instant.now());
            subtaskRepository.save(subtask);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SubtaskResponse detail(Authentication auth, Long actorId, Long taskId, Long subtaskId) {
        Task task = getTaskOrThrow(taskId);
        authorizationService.assertCanView(auth, actorId, task);

        Subtask subtask = getSubtaskOrThrow(subtaskId);
        assertBelongsToTask(subtask, taskId);

        return mapper.toResponse(subtask);
    }

    // ===== helpers =====
    private Task getTaskOrThrow(Long id) {
        return taskRepository.findById(id)
                .filter(t -> !t.isDeleted())
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));
    }

    private Subtask getSubtaskOrThrow(Long id) {
        return subtaskRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUBTASK_NOT_FOUND));
    }

    private void assertBelongsToTask(Subtask subtask, Long taskId) {
        Long actualTaskId = (subtask.getTask() != null) ? subtask.getTask().getId() : null;
        if (actualTaskId == null || !actualTaskId.equals(taskId)) {
            throw new AppException(ErrorCode.SUBTASK_NOT_FOUND);
        }
    }
}
