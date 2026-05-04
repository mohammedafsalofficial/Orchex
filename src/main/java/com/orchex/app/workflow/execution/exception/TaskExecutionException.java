package com.orchex.app.workflow.execution.exception;

import com.orchex.app.common.exception.ErrorCode;
import com.orchex.app.workflow.definition.exception.AppException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

public class TaskExecutionException extends AppException {

    public TaskExecutionException(UUID taskExecutionId, int retries, String cause) {
        super(
                ErrorCode.TASK_EXECUTION_FAILED,
                "Task execution failed after retries",
                List.of(
                        String.format("Task execution id: %s", taskExecutionId),
                        String.format("Failed after %d retries", retries),
                        String.format("Cause: %s", cause)
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
