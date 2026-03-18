package com.orchex.app.workflow.definition.exception;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

public class WorkflowNotFoundException extends AppException {

    public WorkflowNotFoundException(UUID id) {
        super(
                ErrorCode.WORKFLOW_NOT_FOUND,
                "Workflow not found",
                List.of(String.format("Workflow not found for id: %s", id)),
                HttpStatus.NOT_FOUND
        );
    }
}
