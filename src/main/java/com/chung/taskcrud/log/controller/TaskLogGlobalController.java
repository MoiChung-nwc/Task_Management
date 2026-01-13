package com.chung.taskcrud.log.controller;

import com.chung.taskcrud.common.dto.response.ApiResponse;
import com.chung.taskcrud.common.dto.response.PageResponse;
import com.chung.taskcrud.log.dto.response.TaskLogResponse;
import com.chung.taskcrud.log.service.TaskLogService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/logs")
@Tag(name = "Logs", description = "APIs xem lịch sử hoạt động của user")
@SecurityRequirement(name = "bearerAuth")
public class TaskLogGlobalController {

    private final TaskLogService taskLogService;

    private String traceId() {
        return UUID.randomUUID().toString();
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PageResponse<TaskLogResponse>>> myHistory(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest http
    ) {
        Long actorId = (Long) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);

        PageResponse<TaskLogResponse> data = taskLogService.myHistory(authentication, actorId, pageable);
        return ResponseEntity.ok(ApiResponse.success(data, http.getRequestURI(), traceId()));
    }
}
