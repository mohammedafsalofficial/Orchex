package com.orchex.app.workflow.execution.exception;

import com.orchex.app.workflow.definition.exception.AppException;
import com.orchex.app.workflow.definition.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

public class WorkflowExecutionNotFoundException extends AppException {

    public WorkflowExecutionNotFoundException(UUID workflowExecutionId) {
        super(
                ErrorCode.WORKFLOW_EXECUTION_NOT_FOUND,
                "Workflow execution not found",
                List.of(String.format("Workflow execution not found for id: %s", workflowExecutionId)),
                HttpStatus.NOT_FOUND
        );
    }
}
