package com.chung.taskcrud.task.comment.dto.response;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {

    private Long id;
    private Long taskId;
    private Long authorId;
    private String authorEmail;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
}
