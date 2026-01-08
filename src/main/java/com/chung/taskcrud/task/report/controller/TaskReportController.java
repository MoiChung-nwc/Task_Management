package com.chung.taskcrud.task.report.controller;

import com.chung.taskcrud.task.report.service.TaskReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Reports - Tasks", description = "Xuất báo cáo task ra Excel (.xlsx)")
@SecurityRequirement(name = "bearerAuth")
public class TaskReportController {

    private final TaskReportService taskReportService;

    // =========================
    // 1) Export my tasks
    // =========================
    @Operation(
            summary = "Export tasks của tôi (Excel)",
            description = """
                    Xuất danh sách task của user đang đăng nhập ra file Excel (.xlsx).
                    Có thể filter theo status/priority/dueFrom/dueTo/tag/assigneeId.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Excel file generated",
                    content = @Content(
                            mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                            schema = @Schema(type = "string", format = "binary")
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Query param không hợp lệ (date format, ...)",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập / token không hợp lệ",
                    content = @Content
            )
    })
    @GetMapping(
            value = "/me/export",
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )
    public ResponseEntity<byte[]> exportMyTasks(
            @Parameter(hidden = true) Authentication authentication,

            @Parameter(description = "Filter theo status (string)", example = "OPEN", in = ParameterIn.QUERY)
            @RequestParam(required = false) String status,

            @Parameter(description = "Filter theo priority (string)", example = "HIGH", in = ParameterIn.QUERY)
            @RequestParam(required = false) String priority,

            @Parameter(description = "Lọc dueDate từ ngày (yyyy-MM-dd)", example = "2026-01-01", in = ParameterIn.QUERY)
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueFrom,

            @Parameter(description = "Lọc dueDate đến ngày (yyyy-MM-dd)", example = "2026-01-31", in = ParameterIn.QUERY)
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueTo,

            @Parameter(description = "Lọc theo tag", example = "backend", in = ParameterIn.QUERY)
            @RequestParam(required = false) String tag,

            @Parameter(description = "Lọc theo assigneeId (nếu áp dụng)", example = "5", in = ParameterIn.QUERY)
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
    @Operation(
            summary = "Export tasks của user khác (Excel) - SYSTEM_ADMIN",
            description = """
                    Xuất danh sách task của một user bất kỳ ra file Excel (.xlsx).
                    Endpoint này thường chỉ dành cho SYSTEM_ADMIN (tuỳ bạn enforce ở service/controller).
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Excel file generated",
                    content = @Content(
                            mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                            schema = @Schema(type = "string", format = "binary")
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Query param không hợp lệ (date format, ...)",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập / token không hợp lệ",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Không có quyền SYSTEM_ADMIN",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy user / không có dữ liệu",
                    content = @Content
            )
    })
    @GetMapping(
            value = "/users/{userId}/export",
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )
    public ResponseEntity<byte[]> exportUserTasks(
            @Parameter(hidden = true) Authentication authentication,

            @Parameter(description = "User ID cần export", example = "10", in = ParameterIn.PATH)
            @PathVariable Long userId,

            @Parameter(description = "Filter theo status (string)", example = "OPEN", in = ParameterIn.QUERY)
            @RequestParam(required = false) String status,

            @Parameter(description = "Filter theo priority (string)", example = "HIGH", in = ParameterIn.QUERY)
            @RequestParam(required = false) String priority,

            @Parameter(description = "Lọc dueDate từ ngày (yyyy-MM-dd)", example = "2026-01-01", in = ParameterIn.QUERY)
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueFrom,

            @Parameter(description = "Lọc dueDate đến ngày (yyyy-MM-dd)", example = "2026-01-31", in = ParameterIn.QUERY)
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueTo,

            @Parameter(description = "Lọc theo tag", example = "backend", in = ParameterIn.QUERY)
            @RequestParam(required = false) String tag,

            @Parameter(description = "Lọc theo assigneeId (nếu áp dụng)", example = "5", in = ParameterIn.QUERY)
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
        if (bytes == null) bytes = new byte[0];

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
