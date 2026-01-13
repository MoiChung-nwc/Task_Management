package com.chung.taskcrud.log.service;

import com.chung.taskcrud.common.dto.response.PageResponse;
import com.chung.taskcrud.log.dto.response.TaskLogResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface TaskLogService {

    PageResponse<TaskLogResponse> list(
            Authentication auth, Long actorId, Long taskId, Pageable pageable
    );

    TaskLogResponse detail(Authentication auth, Long actorId, Long taskId, Long logId);

    PageResponse<TaskLogResponse> myHistory(Authentication auth, Long actorId, Pageable pageable);

}
