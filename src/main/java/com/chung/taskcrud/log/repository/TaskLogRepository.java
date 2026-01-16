package com.chung.taskcrud.log.repository;

import com.chung.taskcrud.log.entity.TaskLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TaskLogRepository extends JpaRepository<TaskLog, Long> {

    @EntityGraph(attributePaths = {"changes"})
    Page<TaskLog> findAllByTask_IdOrderByCreatedAtDesc(Long taskId, Pageable pageable);

    @EntityGraph(attributePaths = {"changes"})
    Optional<TaskLog> findByIdAndTask_Id(Long logId, Long taskId);

    @EntityGraph(attributePaths = {"changes"})
    @Query("""
        select l
        from TaskLog l
        join l.task t
        where (l.actor.id = :userId)
           or (t.createdBy.id = :userId)
           or (t.assignee.id = :userId)
        order by l.createdAt desc
    """)
    Page<TaskLog> findVisibleLogsForUser(Long userId, Pageable pageable);
}
