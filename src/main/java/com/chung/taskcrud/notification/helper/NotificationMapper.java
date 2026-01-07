package com.chung.taskcrud.notification.helper;

import com.chung.taskcrud.notification.dto.response.NotificationResponse;
import com.chung.taskcrud.notification.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .message(n.getMessage())
                .entityType(n.getEntityType())
                .entityId(n.getEntityId())
                .actorId(n.getActor() != null ? n.getActor().getId() : null)
                .actorEmail(n.getActor() != null ? n.getActor().getEmail() : null)
                .readAt(n.getReadAt())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
