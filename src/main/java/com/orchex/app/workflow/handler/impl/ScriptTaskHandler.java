package com.orchex.app.workflow.handler.impl;

import com.orchex.app.workflow.definition.model.TaskDefinition;
import com.orchex.app.workflow.definition.model.TaskType;
import com.orchex.app.workflow.execution.model.TaskExecution;
import com.orchex.app.workflow.handler.TaskHandler;
import org.springframework.stereotype.Component;

@Component
public class ScriptTaskHandler implements TaskHandler {

    @Override public TaskType getTaskType() {
        return TaskType.SCRIPT;
    }

    @Override public void execute(TaskExecution taskExecution, TaskDefinition taskDefinition) {
        System.out.println("Executing Script task: " + taskDefinition.getName());
        taskExecution.setOutputPayload("Script task completed");
    }
}
