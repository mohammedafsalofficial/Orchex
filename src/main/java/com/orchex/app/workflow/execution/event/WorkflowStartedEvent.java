package com.orchex.app.workflow.execution.event;

import java.util.UUID;

public record WorkflowStartedEvent(UUID workflowExecutionId) {
}
