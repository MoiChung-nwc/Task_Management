package com.chung.taskcrud.task.comment.helper;

import com.chung.taskcrud.task.comment.dto.response.CommentResponse;
import com.chung.taskcrud.task.comment.entity.TaskComment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public CommentResponse toResponse(TaskComment c) {
        return CommentResponse.builder()
                .id(c.getId())
                .taskId(c.getTask() != null ? c.getTask().getId() : null)
                .authorId(c.getAuthor() != null ? c.getAuthor().getId() : null)
                .authorEmail(c.getAuthor() != null ? c.getAuthor().getEmail() : null)
                .content(c.getContent())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
