package com.chung.taskcrud.task.comment.repository;

import com.chung.taskcrud.task.comment.entity.TaskComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {

    Optional<TaskComment> findByIdAndDeletedAtIsNull(Long id);

    Page<TaskComment> findAllByTask_IdAndDeletedAtIsNull(Long taskId, Pageable pageable);
}
