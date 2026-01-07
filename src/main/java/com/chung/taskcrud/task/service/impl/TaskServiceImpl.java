package com.chung.taskcrud.task.service.impl;

import com.chung.taskcrud.auth.entity.User;
import com.chung.taskcrud.auth.repository.UserRepository;
import com.chung.taskcrud.common.dto.response.PageResponse;
import com.chung.taskcrud.common.exception.AppException;
import com.chung.taskcrud.common.exception.ErrorCode;
import com.chung.taskcrud.log.entity.TaskLogEventType;
import com.chung.taskcrud.log.helper.TaskLogHelper;
import com.chung.taskcrud.notification.entity.NotificationType;
import com.chung.taskcrud.notification.helper.NotificationHelper;
import com.chung.taskcrud.task.comment.dto.response.CommentResponse;
import com.chung.taskcrud.task.comment.helper.CommentMapper;
import com.chung.taskcrud.task.comment.repository.TaskCommentRepository;
import com.chung.taskcrud.task.dto.request.*;
import com.chung.taskcrud.task.dto.response.TaskDetailResponse;
import com.chung.taskcrud.task.dto.response.TaskResponse;
import com.chung.taskcrud.task.entity.Task;
import com.chung.taskcrud.task.entity.TaskPriority;
import com.chung.taskcrud.task.entity.TaskStatus;
import com.chung.taskcrud.task.helper.TaskMapper;
import com.chung.taskcrud.task.helper.TaskQueryHelper;
import com.chung.taskcrud.task.helper.TaskTagHelper;
import com.chung.taskcrud.task.repository.TaskRepository;
import com.chung.taskcrud.task.security.TaskAuthorizationService;
import com.chung.taskcrud.task.service.TaskService;
import com.chung.taskcrud.task.subtask.dto.response.SubtaskResponse;
import com.chung.taskcrud.task.subtask.helper.SubtaskMapper;
import com.chung.taskcrud.task.subtask.repository.SubtaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskAuthorizationService authorizationService;

    private final TaskMapper taskMapper;
    private final TaskTagHelper tagHelper;
    private final TaskQueryHelper queryHelper;

    private final SubtaskRepository subtaskRepository;
    private final SubtaskMapper subtaskMapper;

    private final TaskCommentRepository taskCommentRepository;
    private final CommentMapper commentMapper;

    private final NotificationHelper notificationHelper;
    private final TaskLogHelper taskLogHelper;

    @Override
    public TaskResponse create(Authentication auth, Long actorId, CreateTaskRequest request) {
        User creator = getUserOrThrow(actorId);

        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = getUserOrThrow(request.getAssigneeId());
        }

        Task task = Task.builder()
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
                .priority(request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM)
                .dueDate(request.getDueDate())
                .createdBy(creator)
                .assignee(assignee)
                .build();

        task.setTags(tagHelper.resolveTags(request.getTags()));
        taskRepository.save(task);

        notificationHelper.notifyTaskEvent(task, actorId, NotificationType.TASK_CREATED);
        taskLogHelper.logSimple(task, actorId, TaskLogEventType.TASK_CREATED);

        return taskMapper.toResponse(task);
    }

    @Override
    public TaskResponse update(Authentication auth, Long actorId, Long taskId, UpdateTaskRequest request) {
        Task task = getTaskOrThrow(taskId);
        authorizationService.assertCanModify(auth, actorId, task);

        String oldTitle = task.getTitle();
        String oldDesc = task.getDescription();
        TaskStatus oldStatus = task.getStatus();
        TaskPriority oldPriority = task.getPriority();
        LocalDate oldDueDate = task.getDueDate();
        Long oldAssigneeId = (task.getAssignee() != null) ? task.getAssignee().getId() : null;
        String oldTags = tagsToString(task);

        if (request.getTitle() != null) task.setTitle(request.getTitle().trim());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getStatus() != null) task.setStatus(request.getStatus());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());

        if (request.getAssigneeId() != null) {
            task.setAssignee(getUserOrThrow(request.getAssigneeId()));
        }

        if (request.getTags() != null) {
            task.setTags(tagHelper.resolveTags(request.getTags()));
        }

        taskRepository.save(task);

        notificationHelper.notifyTaskEvent(task, actorId, NotificationType.TASK_UPDATED);

        Long newAssigneeId = (task.getAssignee() != null) ? task.getAssignee().getId() : null;
        String newTags = tagsToString(task);

        taskLogHelper.logWithChanges(
                task, actorId, TaskLogEventType.TASK_UPDATED,
                taskLogHelper.change("title", oldTitle, task.getTitle()),
                taskLogHelper.change("description", oldDesc, task.getDescription()),
                taskLogHelper.change("status", oldStatus, task.getStatus()),
                taskLogHelper.change("priority", oldPriority, task.getPriority()),
                taskLogHelper.change("dueDate", oldDueDate, task.getDueDate()),
                taskLogHelper.change("assigneeId", oldAssigneeId, newAssigneeId),
                taskLogHelper.change("tags", oldTags, newTags)
        );

        return taskMapper.toResponse(task);
    }

    @Override
    public void softDelete(Authentication auth, Long actorId, Long taskId) {
        Task task = getTaskOrThrow(taskId);
        authorizationService.assertCanDelete(auth, actorId, task);

        if (!task.isDeleted()) {
            Instant oldDeletedAt = task.getDeletedAt();

            task.setDeletedAt(Instant.now());
            taskRepository.save(task);

            notificationHelper.notifyTaskEvent(task, actorId, NotificationType.TASK_DELETED);

            taskLogHelper.logWithChanges(
                    task, actorId, TaskLogEventType.TASK_DELETED,
                    taskLogHelper.change("deletedAt", oldDeletedAt, task.getDeletedAt())
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> list(
            Authentication auth,
            Long actorId,
            TaskStatus status,
            TaskPriority priority,
            LocalDate dueFrom,
            LocalDate dueTo,
            String tag,
            Long assigneeId,
            Pageable pageable
    ) {
        Specification<Task> spec = queryHelper.buildSpec(auth, actorId, status, priority, dueFrom, dueTo, tag, assigneeId);
        Page<Task> page = taskRepository.findAll(spec, pageable);

        List<TaskResponse> items = page.getContent().stream()
                .map(taskMapper::toResponse)
                .toList();

        return PageResponse.<TaskResponse>builder()
                .items(items)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDetailResponse detail(Authentication auth, Long actorId, Long taskId) {
        Task task = getTaskOrThrow(taskId);
        authorizationService.assertCanView(auth, actorId, task);

        List<SubtaskResponse> subtasks = subtaskRepository
                .findAllByTask_IdAndDeletedAtIsNull(taskId)
                .stream()
                .map(subtaskMapper::toResponse)
                .toList();

        List<CommentResponse> comments = taskCommentRepository
                .findAllByTask_IdAndDeletedAtIsNull(taskId, PageRequest.of(0, 50))
                .getContent()
                .stream()
                .map(commentMapper::toResponse)
                .toList();

        return TaskDetailResponse.builder()
                .task(taskMapper.toResponse(task))
                .subtasks(subtasks)
                .comments(comments)
                .build();
    }

    @Override
    public TaskResponse assign(Authentication auth, Long actorId, Long taskId, AssignTaskRequest request) {
        Task task = getTaskOrThrow(taskId);
        authorizationService.assertCanModify(auth, actorId, task);

        Long oldAssigneeId = (task.getAssignee() != null) ? task.getAssignee().getId() : null;

        task.setAssignee(getUserOrThrow(request.getAssigneeId()));
        taskRepository.save(task);

        notificationHelper.notifyTaskEvent(task, actorId, NotificationType.TASK_ASSIGNED);

        Long newAssigneeId = (task.getAssignee() != null) ? task.getAssignee().getId() : null;
        taskLogHelper.logWithChanges(
                task, actorId, TaskLogEventType.TASK_ASSIGNED,
                taskLogHelper.change("assigneeId", oldAssigneeId, newAssigneeId)
        );

        return taskMapper.toResponse(task);
    }

    @Override
    public TaskResponse updateStatus(Authentication auth, Long actorId, Long taskId, UpdateTaskStatusRequest request) {
        Task task = getTaskOrThrow(taskId);
        authorizationService.assertCanModify(auth, actorId, task);

        TaskStatus oldStatus = task.getStatus();

        task.setStatus(request.getStatus());
        taskRepository.save(task);

        notificationHelper.notifyTaskEvent(task, actorId, NotificationType.TASK_STATUS_UPDATED);

        taskLogHelper.logWithChanges(
                task, actorId, TaskLogEventType.TASK_STATUS_UPDATED,
                taskLogHelper.change("status", oldStatus, task.getStatus())
        );

        return taskMapper.toResponse(task);
    }

    private Task getTaskOrThrow(Long id) {
        return taskRepository.findById(id)
                .filter(t -> !t.isDeleted())
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VALIDATION_ERROR, "User not found"));
    }

    // fix join tags
    private String tagsToString(Task task) {
        if (task.getTags() == null || task.getTags().isEmpty()) return "";
        return task.getTags().stream()
                .map(t -> t.getName())
                .sorted()
                .reduce((a, b) -> a + "," + b)
                .orElse("");
    }
}
