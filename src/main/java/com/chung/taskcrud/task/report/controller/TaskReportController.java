package com.chung.taskcrud.task.report.controller;

import com.chung.taskcrud.task.report.service.TaskReportService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports/tasks")
public class TaskReportController {

    private final TaskReportService taskReportService;

    // =========================
    // 1) Export my tasks
    // =========================
    @GetMapping(value = "/me/export",
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportMyTasks(
            Authentication authentication,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueTo,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) Long assigneeId,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();

        byte[] bytes = taskReportService.exportMyTasks(
                authentication, actorId, status, priority, dueFrom, dueTo, tag, assigneeId
        );

        return buildExcelResponse(bytes, "tasks_report_user_" + actorId);
    }

    // =========================
    // 2) Export tasks of another user (SYSTEM_ADMIN only)
    // =========================
    @GetMapping(value = "/users/{userId}/export",
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportUserTasks(
            Authentication authentication,
            @PathVariable Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueTo,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) Long assigneeId,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();

        byte[] bytes = taskReportService.exportUserTasks(
                authentication, actorId, userId, status, priority, dueFrom, dueTo, tag, assigneeId
        );

        return buildExcelResponse(bytes, "tasks_report_user_" + userId);
    }

    // =========================
    // Helper: build excel response
    // =========================
    private ResponseEntity<byte[]> buildExcelResponse(byte[] bytes, String baseName) {
        if (bytes == null) bytes = new byte[0]; // tránh NPE dù không nên xảy ra

        String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String filename = baseName + "_" + date + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        ));
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        headers.setContentLength(bytes.length);

        return ResponseEntity.ok().headers(headers).body(bytes);
    }
}
