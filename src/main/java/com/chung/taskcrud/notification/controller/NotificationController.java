package com.chung.taskcrud.notification.controller;

import com.chung.taskcrud.common.dto.response.ApiResponse;
import com.chung.taskcrud.common.dto.response.PageResponse;
import com.chung.taskcrud.notification.dto.response.NotificationResponse;
import com.chung.taskcrud.notification.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    private String traceId() {
        return UUID.randomUUID().toString();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> list (
            Authentication authentication,
            @RequestParam(defaultValue = "fales") boolean unreadOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);

        PageResponse<NotificationResponse> data = notificationService.listMyNotifications(authentication, actorId, unreadOnly, pageable);

        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(
            Authentication authentication,
            @PathVariable("id") Long id,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        NotificationResponse data = notificationService.markAsRead(authentication, actorId, id);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }
}
