package com.chung.taskcrud.log.repository;

import com.chung.taskcrud.log.entity.TaskLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TaskLogRepository extends JpaRepository<TaskLog, Long> {

    Page<TaskLog> findAllByTask_IdOrderByCreatedAtDesc(Long taskId, Pageable pageable);

    Optional<TaskLog> findByIdAndTask_Id(Long id, Long taskId);

    @Query("""
        select l
        from TaskLog l
        join l.task t
        left join l.actor a
        where (a.id = :userId) or (t.assignee.id = :userId)
        order by l.createdAt desc
    """)
    Page<TaskLog> findVisibleLogsForUser(@Param("userId") Long userId, Pageable pageable);
}
