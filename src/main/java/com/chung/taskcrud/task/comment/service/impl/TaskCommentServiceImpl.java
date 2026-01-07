package com.chung.taskcrud.task.comment.service.impl;

import com.chung.taskcrud.auth.entity.User;
import com.chung.taskcrud.auth.repository.UserRepository;
import com.chung.taskcrud.common.dto.response.PageResponse;
import com.chung.taskcrud.common.exception.AppException;
import com.chung.taskcrud.common.exception.ErrorCode;
import com.chung.taskcrud.notification.entity.NotificationType;
import com.chung.taskcrud.notification.helper.NotificationHelper;
import com.chung.taskcrud.task.comment.dto.request.CreateCommentRequest;
import com.chung.taskcrud.task.comment.dto.request.UpdateCommentRequest;
import com.chung.taskcrud.task.comment.dto.response.CommentResponse;
import com.chung.taskcrud.task.comment.entity.TaskComment;
import com.chung.taskcrud.task.comment.helper.CommentMapper;
import com.chung.taskcrud.task.comment.repository.TaskCommentRepository;
import com.chung.taskcrud.task.comment.service.TaskCommentService;
import com.chung.taskcrud.task.entity.Task;
import com.chung.taskcrud.task.repository.TaskRepository;
import com.chung.taskcrud.task.security.TaskAuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskCommentServiceImpl implements TaskCommentService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskCommentRepository commentRepository;

    private final TaskAuthorizationService authorizationService;

    private final CommentMapper mapper;

    private final NotificationHelper notificationHelper;

    @Override
    public CommentResponse create(Authentication auth, Long actorId, Long taskId, CreateCommentRequest request) {
        Task task = getTaskOrThrow(taskId);
        authorizationService.assertCanModify(auth, actorId, task);

        User author = getUserOrThrow(actorId);

        TaskComment comment = TaskComment.builder()
                .task(task)
                .author(author)
                .content(request.getContent().trim())
                .build();

        commentRepository.save(comment);

        notificationHelper.notifyTaskEvent(task, actorId, NotificationType.COMMENT_CREATED);

        return mapper.toResponse(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> list(Authentication auth, Long actorId, Long taskId, Pageable pageable) {
        Task task = getTaskOrThrow(taskId);
        authorizationService.assertCanView(auth, actorId, task);

        Page<TaskComment> page = commentRepository.findAllByTask_IdAndDeletedAtIsNull(taskId, pageable);
        List<CommentResponse> items = page.getContent().stream()
                .map(mapper::toResponse)
                .toList();

        return PageResponse.<CommentResponse>builder()
                .items(items)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponse detail(Authentication auth, Long actorId, Long taskId, Long commentId) {
        Task task = getTaskOrThrow(taskId);
        authorizationService.assertCanView(auth, actorId, task);

        TaskComment comment = getCommentOrThrow(commentId);
        assertBelongsToTask(comment, taskId);

        return mapper.toResponse(comment);
    }

    @Override
    public CommentResponse update(Authentication auth, Long actorId, Long taskId, Long commentId, UpdateCommentRequest request) {
        Task task = getTaskOrThrow(taskId);
        authorizationService.assertCanModify(auth, actorId, task);

        TaskComment comment = getCommentOrThrow(commentId);
        assertBelongsToTask(comment, taskId);

        assertCanEditComment(auth, actorId, comment);

        comment.setContent(request.getContent().trim());
        commentRepository.save(comment);

        return mapper.toResponse(comment);
    }

    @Override
    public void delete(Authentication auth, Long actorId, Long taskId, Long commentId) {
        Task task = getTaskOrThrow(taskId);
        authorizationService.assertCanDelete(auth, actorId, task);

        TaskComment comment = getCommentOrThrow(commentId);
        assertBelongsToTask(comment, taskId);

        assertCanEditComment(auth, actorId, comment);

        if (!comment.isDeleted()) {
            comment.setDeletedAt(Instant.now());
            commentRepository.save(comment);
        }
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

    private TaskComment getCommentOrThrow(Long id) {
        return commentRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private void assertBelongsToTask(TaskComment comment, Long taskId) {
        Long actualTaskId = comment.getTask() != null ? comment.getTask().getId() : null;
        if (actualTaskId == null || !actualTaskId.equals(taskId)) {
            throw new AppException(ErrorCode.COMMENT_NOT_FOUND);
        }
    }

    private void assertCanEditComment(Authentication auth, Long actorId, TaskComment comment) {
        if (authorizationService.isSystemAdmin(auth)) return;

        Long authorId = comment.getAuthor() != null ? comment.getAuthor().getId() : null;
        if (authorId == null || !authorId.equals(actorId)) {
            throw new AppException(ErrorCode.COMMENT_ACCESS_DENIED, "You can only edit/delete your own comment");
        }
    }
}
