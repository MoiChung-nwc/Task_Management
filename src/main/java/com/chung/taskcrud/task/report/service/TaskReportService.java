package com.chung.taskcrud.task.report.service;

import org.springframework.security.core.Authentication;

import java.time.LocalDate;

public interface TaskReportService {

    byte[] exportMyTasks(
            Authentication auth,
            Long actorId,
            String status,
            String priority,
            LocalDate dueFrom,
            LocalDate dueTo,
            String tag,
            Long assigneeId
    );

    byte[] exportUserTasks(
            Authentication auth,
            Long actorId,
            Long userId,
            String status,
            String priority,
            LocalDate dueFrom,
            LocalDate dueTo,
            String tag,
            Long assigneeId
    );
}
