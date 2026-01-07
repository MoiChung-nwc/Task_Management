package com.chung.taskcrud.task.subtask.repository;

import com.chung.taskcrud.task.subtask.entity.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubtaskRepository extends JpaRepository<Subtask, Long> {

    Optional<Subtask> findByIdAndDeletedAtIsNull(Long id);

    List<Subtask> findAllByTask_IdAndDeletedAtIsNull(Long taskId);
}
