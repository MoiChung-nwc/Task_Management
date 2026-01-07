package com.chung.taskcrud.notification.dto.response;

import com.chung.taskcrud.notification.entity.NotificationType;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private String entityType;
    private Long entityId;
    private Long actorId;
    private String actorEmail;
    private Instant readAt;
    private Instant createdAt;
}
