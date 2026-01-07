package com.chung.taskcrud.notification.repository;

import com.chung.taskcrud.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findAllByRecipient_IdOrderByCreatedAtDesc(Long recipientId, Pageable pageable);

    Page<Notification> findAllByRecipient_IdAndReadAtIsNullOrderByCreatedAtDesc(Long recipientId, Pageable pageable);
}
