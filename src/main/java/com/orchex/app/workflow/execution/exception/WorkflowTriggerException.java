package com.orchex.app.workflow.execution.exception;

import com.orchex.app.common.exception.ErrorCode;
import com.orchex.app.workflow.definition.exception.AppException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

public class WorkflowTriggerException extends AppException {

    public WorkflowTriggerException(UUID workflowId, String reason) {
        super(
                ErrorCode.WORKFLOW_TRIGGER_FAILED,
                "Failed to trigger workflow",
                List.of(
                        String.format("Workflow id: %s", workflowId),
                        String.format("Reason: %s", reason)
                ),
                HttpStatus.CONFLICT
        );
    }
}
