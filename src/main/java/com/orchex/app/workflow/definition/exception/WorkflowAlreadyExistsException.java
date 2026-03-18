package com.orchex.app.workflow.definition.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class WorkflowAlreadyExistsException extends AppException {

    public WorkflowAlreadyExistsException(String workflowName) {
        super(
                ErrorCode.WORKFLOW_ALREADY_EXISTS,
                "Workflow already exists",
                List.of(String.format("Workflow with name '%s' already exists", workflowName)),
                HttpStatus.CONFLICT
        );
    }
}
