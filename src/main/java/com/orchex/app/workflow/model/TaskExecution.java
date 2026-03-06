package com.orchex.app.workflow.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "task_executions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_execution_id", nullable = false)
    private WorkflowExecution workflowExecution;

    @Column(nullable = false)
    private String taskName;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private Integer retryCount;

    private LocalDateTime startedAt;

    private LocalDateTime createdAt;

    private String workerId;
}
