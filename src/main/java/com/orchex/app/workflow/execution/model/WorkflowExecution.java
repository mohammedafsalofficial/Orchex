package com.orchex.app.workflow.execution.model;

import com.orchex.app.workflow.definition.model.WorkflowDefinition;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "workflow_executions", indexes = {
        @Index(name = "idx_workflow_execution_status", columnList = "status"),
        @Index(name = "idx_workflow_execution_definition", columnList = "workflow_definition_id"),
        @Index(name = "idx_workflow_execution_started_at", columnList = "startedAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_definition_id", nullable = false)
    private WorkflowDefinition workflowDefinition;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkflowStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    // Correlation ID for tracing across distributed systems
    @Column(nullable = false)
    private String correlationId;

    // Captures the top-level error message if the workflow fails
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(columnDefinition = "TEXT")
    private String inputPayload;

    // e.g. "USER", "SCHEDULER", "API"
    @Column(nullable = false)
    private String triggeredBy;

    @OneToMany(mappedBy = "workflowExecution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TaskExecution> taskExecutions;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = WorkflowStatus.PENDING;
        }
    }
}
