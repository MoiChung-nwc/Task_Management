package com.chung.taskcrud.task.comment.service;

import com.chung.taskcrud.common.dto.response.PageResponse;
import com.chung.taskcrud.task.comment.dto.request.CreateCommentRequest;
import com.chung.taskcrud.task.comment.dto.request.UpdateCommentRequest;
import com.chung.taskcrud.task.comment.dto.response.CommentResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface TaskCommentService {

    CommentResponse create(Authentication auth, Long actorId, Long taskId, CreateCommentRequest request);

    PageResponse<CommentResponse> list(Authentication auth, Long actorId, Long taskId, Pageable pageable);

    CommentResponse detail(Authentication auth, Long actorId, Long taskId, Long commentId);

    CommentResponse update(Authentication auth, Long actorId, Long taskId, Long commentId, UpdateCommentRequest request);

    void delete(Authentication auth, Long actorId, Long taskId, Long commentId);
}
