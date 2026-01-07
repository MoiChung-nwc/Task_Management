package com.chung.taskcrud.notification.helper;

import com.chung.taskcrud.auth.entity.User;
import com.chung.taskcrud.auth.repository.UserRepository;
import com.chung.taskcrud.notification.entity.Notification;
import com.chung.taskcrud.notification.entity.NotificationType;
import com.chung.taskcrud.notification.repository.NotificationRepository;
import com.chung.taskcrud.task.entity.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class NotificationHelper {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void notifyTaskEvent(Task task, Long actorId, NotificationType type) {

        if(task == null) {
            return;
        }

        User actor = (actorId != null) ? userRepository.findById(actorId).orElse(null) : null;

        Set<Long> recipientIds = new LinkedHashSet<>();

        if(task.getCreatedBy() != null) recipientIds.add(task.getCreatedBy().getId());
        if(task.getAssignee() != null) recipientIds.add(task.getAssignee().getId());
        if(actorId != null) recipientIds.remove(actorId);
        if(recipientIds.isEmpty()) return;

        String title = buildTitle(type);
        String msg = buildMessage(type, actor, task);

        for (Long rid: recipientIds) {
            User recipent = userRepository.findById(rid).orElse(null);
            if(recipent == null) continue;;

            Notification n = Notification.builder()
                    .recipient(recipent)
                    .type(type)
                    .title(title)
                    .message(msg)
                    .entityType("TASK")
                    .entityId(task.getId())
                    .actor(actor)
                    .build();

            notificationRepository.save(n);
        }
    }

    private String buildTitle(NotificationType type) {
        return switch (type) {
            case TASK_CREATED -> "New task created";
            case TASK_UPDATED -> "Task updated";
            case TASK_ASSIGNED -> "Task assigned";
            case TASK_STATUS_UPDATED -> "Task status changed";
            case TASK_DELETED -> "Task deleted";
            case SUBTASK_CREATED -> "New subtask";
            case SUBTASK_UPDATED -> "Subtask updated";
            case SUBTASK_DELETED -> "Subtask deleted";
            case COMMENT_CREATED -> "New comment";
        };
    }

    private String buildMessage(NotificationType type, User actor, Task task) {
        String actorEmail = (actor != null ? actor.getEmail() : "Someone");
        String title = (task != null ? task.getTitle() : "");
        Long taskId = (task != null ? task.getId() : null);

        return switch (type) {
            case TASK_CREATED ->
                    actorEmail + " created task #" + taskId + ": \"" + title + "\"";
            case TASK_ASSIGNED ->
                    actorEmail + " assigned you to task #" + taskId + ": \"" + title + "\"";
            case TASK_STATUS_UPDATED ->
                    actorEmail + " changed status of task #" + taskId + ": \"" + title + "\"";
            case TASK_UPDATED ->
                    actorEmail + " updated task #" + taskId + ": \"" + title + "\"";
            case TASK_DELETED ->
                    actorEmail + " deleted task #" + taskId + ": \"" + title + "\"";
            case SUBTASK_CREATED ->
                    actorEmail + " created a subtask in task #" + taskId + ": \"" + title + "\"";
            case SUBTASK_UPDATED ->
                    actorEmail + " updated a subtask in task #" + taskId + ": \"" + title + "\"";
            case SUBTASK_DELETED ->
                    actorEmail + " deleted a subtask in task #" + taskId + ": \"" + title + "\"";
            case COMMENT_CREATED ->
                    actorEmail + " commented on task #" + taskId + ": \"" + title + "\"";
        };
    }
}
