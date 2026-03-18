package com.orchex.app.workflow.definition.exception;

import lombok.Getter;

@Getter
public class WorkflowAlreadyExistsException extends RuntimeException {

    private final ErrorCode errorCode;

    public WorkflowAlreadyExistsException(String message) {
        super(message);
        this.errorCode = ErrorCode.WORKFLOW_ALREADY_EXISTS;
    }
}
