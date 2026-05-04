package com.orchex.app.workflow.handler.exception;

import com.orchex.app.common.exception.ErrorCode;
import com.orchex.app.workflow.definition.exception.AppException;
import com.orchex.app.workflow.definition.model.TaskType;
import org.springframework.http.HttpStatus;

import java.util.List;

public class TaskHandlerNotFoundException extends AppException {

    public TaskHandlerNotFoundException(TaskType taskType) {
        super(
                ErrorCode.TASK_HANDLER_NOT_FOUND,
                "No handler registered for task type",
                List.of(String.format("No handler found for task type: %s", taskType)),
                HttpStatus.NOT_FOUND
        );
    }
}
