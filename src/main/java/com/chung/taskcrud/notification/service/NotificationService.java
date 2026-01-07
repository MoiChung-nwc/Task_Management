package com.chung.taskcrud.notification.service;

import com.chung.taskcrud.common.dto.response.PageResponse;
import com.chung.taskcrud.notification.dto.response.NotificationResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface NotificationService {

    PageResponse<NotificationResponse> listMyNotifications(
            Authentication auth, Long actorId, boolean unreadOnly, Pageable pageable
    );

    NotificationResponse markAsRead(Authentication auth, Long actorId, Long notificationId);
}
