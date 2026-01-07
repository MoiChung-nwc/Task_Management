package com.chung.taskcrud.log.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "task_log_changes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskLogChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "task_log_id")
    private TaskLog taskLog;

    @Column(nullable = false, length = 100)
    private String fieldName;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String oldValue;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String newValue;
}
