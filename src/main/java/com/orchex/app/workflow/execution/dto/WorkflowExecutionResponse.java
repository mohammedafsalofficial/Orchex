package com.orchex.app.workflow.execution.dto;

import com.orchex.app.workflow.execution.model.WorkflowStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class WorkflowExecutionResponse {

    private UUID id;
    private UUID workflowId;
    private WorkflowStatus status;
    private String correlationId;
    private String triggeredBy;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
