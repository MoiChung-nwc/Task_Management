package com.chung.taskcrud.log.entity;


import com.chung.taskcrud.auth.entity.User;
import com.chung.taskcrud.task.entity.Task;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "task_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TaskLogEventType eventType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private User actor;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "taskLog", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TaskLogChange> changes = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }

    public void addChange(TaskLogChange c) {
        c.setTaskLog(this);
        this.changes.add(c);
    }
}
