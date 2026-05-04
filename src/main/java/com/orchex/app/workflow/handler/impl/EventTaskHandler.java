package com.orchex.app.workflow.handler.impl;

import com.orchex.app.workflow.definition.model.TaskDefinition;
import com.orchex.app.workflow.definition.model.TaskType;
import com.orchex.app.workflow.execution.model.TaskExecution;
import com.orchex.app.workflow.handler.TaskHandler;
import org.springframework.stereotype.Component;

@Component
public class EventTaskHandler implements TaskHandler {

    @Override public TaskType getTaskType() {
        return TaskType.EVENT;
    }

    @Override public void execute(TaskExecution taskExecution, TaskDefinition taskDefinition) {
        System.out.println("Executing Event task: " + taskDefinition.getName());
        taskExecution.setOutputPayload("Event task completed");
    }
}
