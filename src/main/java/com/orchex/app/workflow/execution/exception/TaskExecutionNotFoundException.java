package com.orchex.app.workflow.execution.exception;

import com.orchex.app.common.exception.ErrorCode;
import com.orchex.app.workflow.definition.exception.AppException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

public class TaskExecutionNotFoundException extends AppException {

    public TaskExecutionNotFoundException(UUID taskExecutionId) {
        super(
                ErrorCode.TASK_EXECUTION_NOT_FOUND,
                "Task execution not found",
                List.of(String.format("Task execution not found for id: %s", taskExecutionId)),
                HttpStatus.NOT_FOUND
        );
    }
}
