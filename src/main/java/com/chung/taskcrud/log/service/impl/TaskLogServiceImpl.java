package com.chung.taskcrud.log.service.impl;

import com.chung.taskcrud.common.dto.response.PageResponse;
import com.chung.taskcrud.common.exception.AppException;
import com.chung.taskcrud.common.exception.ErrorCode;
import com.chung.taskcrud.log.dto.response.TaskLogResponse;
import com.chung.taskcrud.log.entity.TaskLog;
import com.chung.taskcrud.log.helper.TaskLogMapper;
import com.chung.taskcrud.log.repository.TaskLogRepository;
import com.chung.taskcrud.log.service.TaskLogService;
import com.chung.taskcrud.task.entity.Task;
import com.chung.taskcrud.task.repository.TaskRepository;
import com.chung.taskcrud.task.security.TaskAuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskLogServiceImpl implements TaskLogService {

    private final TaskLogRepository taskLogRepository;
    private final TaskRepository taskRepository;
    private final TaskAuthorizationService authorizationService;
    private final TaskLogMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TaskLogResponse> list(Authentication auth, Long actorId, Long taskId, Pageable pageable) {
        Task task = getTaskIncludingDeletedOrThrow(taskId);
        authorizationService.assertCanView(auth, actorId, task);

        // ✅ bỏ cast Page<?> -> Page<TaskLog>
        Page<TaskLog> p = taskLogRepository.findAllByTask_IdOrderByCreatedAtDesc(taskId, pageable);

        List<TaskLogResponse> items = p.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();

        return PageResponse.<TaskLogResponse>builder()
                .items(items)
                .page(p.getNumber())
                .size(p.getSize())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public TaskLogResponse detail(Authentication auth, Long actorId, Long taskId, Long logId) {
        Task task = getTaskIncludingDeletedOrThrow(taskId);
        authorizationService.assertCanView(auth, actorId, task);

        var log = taskLogRepository.findByIdAndTask_Id(logId, taskId)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_LOG_NOT_FOUND));

        return mapper.toResponse(log);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TaskLogResponse> myHistory(Authentication auth, Long actorId, Pageable pageable) {
        Page<TaskLog> p = taskLogRepository.findVisibleLogsForUser(actorId, pageable);

        List<TaskLogResponse> items = p.getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();

        return PageResponse.<TaskLogResponse>builder()
                .items(items)
                .page(p.getNumber())
                .size(p.getSize())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .build();
    }

    private Task getTaskIncludingDeletedOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));
    }
}
