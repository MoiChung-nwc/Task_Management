package com.chung.taskcrud.log.repository;

import com.chung.taskcrud.log.entity.TaskLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskLogRepository extends JpaRepository<TaskLog, Long> {

    Page<TaskLog> findAllByTask_IdOrderByCreatedAtDesc(Long taskId, Pageable pageable);

    Optional<TaskLog> findByIdAndTask_Id(Long id, Long taskId);
}
