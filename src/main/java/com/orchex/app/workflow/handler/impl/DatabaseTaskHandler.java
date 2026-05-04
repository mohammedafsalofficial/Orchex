package com.orchex.app.workflow.handler.impl;

import com.orchex.app.workflow.definition.model.TaskDefinition;
import com.orchex.app.workflow.definition.model.TaskType;
import com.orchex.app.workflow.execution.model.TaskExecution;
import com.orchex.app.workflow.handler.TaskHandler;
import org.springframework.stereotype.Component;

@Component
public class DatabaseTaskHandler implements TaskHandler {

    @Override public TaskType getTaskType() {
        return TaskType.DATABASE;
    }

    @Override public void execute(TaskExecution taskExecution, TaskDefinition taskDefinition) {
        System.out.println("Executing DB task: " + taskDefinition.getName());
        taskExecution.setOutputPayload("Database task completed");
    }
}
