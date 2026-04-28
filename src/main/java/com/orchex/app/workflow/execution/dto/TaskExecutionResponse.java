package com.orchex.app.workflow.execution.dto;

import com.orchex.app.workflow.execution.model.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TaskExecutionResponse {

    private UUID id;
    private String name;
    private TaskStatus status;

    private Integer retryCount;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    private String workerId;
    private String errorMessage;
}
