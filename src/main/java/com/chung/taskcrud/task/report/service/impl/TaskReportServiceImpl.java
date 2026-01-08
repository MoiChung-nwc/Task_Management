package com.chung.taskcrud.task.report.service.impl;

import com.chung.taskcrud.common.exception.AppException;
import com.chung.taskcrud.common.exception.ErrorCode;
import com.chung.taskcrud.task.entity.Task;
import com.chung.taskcrud.task.entity.TaskPriority;
import com.chung.taskcrud.task.entity.TaskStatus;
import com.chung.taskcrud.task.helper.TaskQueryHelper;
import com.chung.taskcrud.task.report.helper.TaskReportExcelHelper;
import com.chung.taskcrud.task.report.service.TaskReportService;
import com.chung.taskcrud.task.repository.TaskRepository;
import com.chung.taskcrud.task.security.TaskAuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskReportServiceImpl implements TaskReportService {

    private final TaskRepository taskRepository;
    private final TaskQueryHelper queryHelper;
    private final TaskAuthorizationService authorizationService;
    private final TaskReportExcelHelper excelHelper;

    @Override
    public byte[] exportMyTasks(
            Authentication auth,
            Long actorId,
            String status,
            String priority,
            LocalDate dueFrom,
            LocalDate dueTo,
            String tag,
            Long assigneeId
    ) {
        TaskStatus st = parseStatus(status);
        TaskPriority pr = parsePriority(priority);

        Specification<Task> spec = queryHelper.buildSpec(auth, actorId, st, pr, dueFrom, dueTo, tag, assigneeId);
        List<Task> tasks = taskRepository.findAll(spec);

        return excelHelper.buildExcel(tasks);
    }

    @Override
    public byte[] exportUserTasks(
            Authentication auth,
            Long actorId,
            Long userId,
            String status,
            String priority,
            LocalDate dueFrom,
            LocalDate dueTo,
            String tag,
            Long assigneeId
    ) {
        // chỉ SYSTEM_ADMIN được export user khác
        if (!authorizationService.isSystemAdmin(auth)) {
            throw new AppException(ErrorCode.TASK_ACCESS_DENIED, "Only SYSTEM_ADMIN can export other user's report");
        }

        TaskStatus st = parseStatus(status);
        TaskPriority pr = parsePriority(priority);

        // actor scope = userId được export
        Specification<Task> spec = queryHelper.buildSpec(auth, userId, st, pr, dueFrom, dueTo, tag, assigneeId);
        List<Task> tasks = taskRepository.findAll(spec);

        return excelHelper.buildExcel(tasks);
    }

    private TaskStatus parseStatus(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return TaskStatus.valueOf(s.trim().toUpperCase());
        } catch (Exception e) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Invalid status: " + s);
        }
    }

    private TaskPriority parsePriority(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return TaskPriority.valueOf(s.trim().toUpperCase());
        } catch (Exception e) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Invalid priority: " + s);
        }
    }
}
