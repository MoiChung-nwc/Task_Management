package com.chung.taskcrud.log.helper;

import com.chung.taskcrud.auth.entity.User;
import com.chung.taskcrud.auth.repository.UserRepository;
import com.chung.taskcrud.log.entity.TaskLog;
import com.chung.taskcrud.log.entity.TaskLogChange;
import com.chung.taskcrud.log.entity.TaskLogEventType;
import com.chung.taskcrud.log.repository.TaskLogRepository;
import com.chung.taskcrud.task.entity.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TaskLogHelper {

    private final TaskLogRepository taskLogRepository;
    private final UserRepository userRepository;

    @Transactional
    public void logSimple(Task task, Long actorId, TaskLogEventType eventType) {
        if (task == null) {
            return;
        }

        User actor = (actorId != null) ? userRepository.findById(actorId).orElse(null) : null;

        TaskLog log = TaskLog.builder()
                .task(task)
                .actor(actor)
                .eventType(eventType)
                .build();

        taskLogRepository.save(log);
    }

    @Transactional
    public void logWithChanges(Task task, Long actorId, TaskLogEventType eventType, TaskLogChange... changes) {
        if(task == null) {
            return;
        }

        User actor = (actorId != null) ? userRepository.findById(actorId).orElse(null) : null;

        TaskLog log = TaskLog.builder()
                .task(task)
                .actor(actor)
                .eventType(eventType)
                .build();

        if(changes != null) {
            for (TaskLogChange c : changes) {
                if (c == null) continue;
                log.addChange(c);
            }
        }

        taskLogRepository.save(log);
    }

    public TaskLogChange change(String field, Object oldVal, Object newVal) {
        String o = (oldVal == null) ? null : oldVal.toString();
        String n = (newVal == null) ? null : newVal.toString();

        if(Objects.equals(o, n)) return null;

        return TaskLogChange.builder()
                .fieldName(field)
                .oldValue(o)
                .newValue(n)
                .build();
    }
}
