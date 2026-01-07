package com.chung.taskcrud.notification.service.impl;

import com.chung.taskcrud.common.dto.response.PageResponse;
import com.chung.taskcrud.common.exception.AppException;
import com.chung.taskcrud.common.exception.ErrorCode;
import com.chung.taskcrud.notification.dto.response.NotificationResponse;
import com.chung.taskcrud.notification.entity.Notification;
import com.chung.taskcrud.notification.helper.NotificationMapper;
import com.chung.taskcrud.notification.repository.NotificationRepository;
import com.chung.taskcrud.notification.service.NotificationService;
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
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> listMyNotifications(
            Authentication auth, Long actorId, boolean unreadOnly, Pageable pageable
    ) {
        Page<Notification> page = unreadOnly
                ? notificationRepository.findAllByRecipient_IdAndReadAtIsNullOrderByCreatedAtDesc(actorId, pageable)
                : notificationRepository.findAllByRecipient_IdOrderByCreatedAtDesc(actorId, pageable);

        List<NotificationResponse> items = page.getContent().stream()
                .map(mapper::toResponse)
                .toList();

        return PageResponse.<NotificationResponse>builder()
                .items(items)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public NotificationResponse markAsRead(Authentication auth, Long actorId, Long notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        Long ownerId = (n.getRecipient() != null) ? n.getRecipient().getId() : null;
        if (ownerId == null || !ownerId.equals(actorId)) {
            throw new AppException(ErrorCode.NOTIFICATION_ACCESS_DENIED, "You can only read your own notifications");
        }

        if (n.getReadAt() == null) {
            n.setReadAt(Instant.now());
            notificationRepository.save(n);
        }

        return mapper.toResponse(n);
    }
}
