package com.orchex.app.workflow.engine.service;

import com.orchex.app.workflow.definition.model.TaskDefinition;
import com.orchex.app.workflow.execution.exception.WorkflowExecutionNotFoundException;
import com.orchex.app.workflow.execution.model.TaskExecution;
import com.orchex.app.workflow.execution.model.TaskStatus;
import com.orchex.app.workflow.execution.model.WorkflowExecution;
import com.orchex.app.workflow.execution.model.WorkflowStatus;
import com.orchex.app.workflow.execution.repository.WorkflowExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowEngineService {

    private final WorkflowExecutionRepository workflowExecutionRepository;

    public void executeAsync(UUID workflowExecutionId) {
        WorkflowExecution workflowExecution = workflowExecutionRepository.findById(workflowExecutionId)
                .orElseThrow(() -> new WorkflowExecutionNotFoundException(workflowExecutionId));

        workflowExecution.setStatus(WorkflowStatus.RUNNING);
        workflowExecution.setStartedAt(LocalDateTime.now());

        List<TaskDefinition> tasksDefinitions = workflowExecution.getWorkflowDefinition()
                .getTasks()
                .stream()
                .sorted(Comparator.comparing(TaskDefinition::getStepOrder))
                .toList();

        for (TaskDefinition taskDefinition : tasksDefinitions) {
            TaskExecution taskExecution = createTaskExecution(taskDefinition);

            try {
                executeTask(taskExecution, taskDefinition);
                taskExecution.setStatus(TaskStatus.COMPLETED);
            } catch (Exception exception) {
                taskExecution.setStatus(TaskStatus.FAILED);
                workflowExecution.setStatus(WorkflowStatus.FAILED);
                workflowExecution.setErrorMessage(exception.getMessage());
                break;
            }
        }

        if (workflowExecution.getStatus() != WorkflowStatus.FAILED) {
            workflowExecution.setStatus(WorkflowStatus.COMPLETED);
        }

        workflowExecution.setCompletedAt(LocalDateTime.now());
    }

    private TaskExecution createTaskExecution(TaskDefinition taskDefinition) {

    }

    private void executeTask(TaskExecution taskExecution, TaskDefinition taskDefinition) {

    }
}
