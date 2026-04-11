package com.orchex.app.workflow.engine.service;

import com.orchex.app.workflow.definition.model.TaskDefinition;
import com.orchex.app.workflow.execution.model.TaskExecution;
import com.orchex.app.workflow.execution.repository.WorkflowExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowEngineService {

    private final WorkflowExecutionRepository workflowExecutionRepository;

    public void executeAsync(UUID workflowExecutionId) {

    }

    private TaskExecution createTaskExecution(TaskDefinition taskDefinition) {
        return null;
    }

    private void executeTask(TaskExecution taskExecution, TaskDefinition taskDefinition) {

    }
}
