package com.orchex.app.workflow.handler;

import com.orchex.app.workflow.definition.model.TaskDefinition;
import com.orchex.app.workflow.definition.model.TaskType;
import com.orchex.app.workflow.execution.model.TaskExecution;

public interface TaskHandler {

    TaskType getTaskType();
    void execute(TaskExecution taskExecution, TaskDefinition taskDefinition);
}
