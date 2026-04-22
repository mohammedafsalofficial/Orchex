package com.orchex.app.workflow.engine.service;

import com.orchex.app.workflow.definition.model.TaskDefinition;
import com.orchex.app.workflow.execution.exception.WorkflowExecutionNotFoundException;
import com.orchex.app.workflow.execution.model.TaskExecution;
import com.orchex.app.workflow.execution.model.WorkflowExecution;
import com.orchex.app.workflow.execution.model.WorkflowStatus;
import com.orchex.app.workflow.execution.repository.TaskExecutionRepository;
import com.orchex.app.workflow.execution.repository.WorkflowExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowEngineService {

    private final WorkflowExecutionRepository workflowExecutionRepository;
    private final TaskExecutionRepository taskExecutionRepository;
    private final TaskExecutionRunner taskExecutionRunner;

    @Transactional
    public void executeAsync(UUID workflowExecutionId) {
        WorkflowExecution workflowExecution = workflowExecutionRepository.findById(workflowExecutionId)
                .orElseThrow(() -> new WorkflowExecutionNotFoundException(workflowExecutionId));

        if (workflowExecution.getTaskExecutions() == null || workflowExecution.getTaskExecutions().isEmpty()) {
            List<TaskExecution> taskExecutions = workflowExecution.getWorkflowDefinition()
                    .getTasks()
                    .stream()
                    .map(taskDefinition -> createTaskExecution(workflowExecution, taskDefinition))
                    .toList();

            taskExecutionRepository.saveAll(taskExecutions);
        }

        workflowExecution.setStatus(WorkflowStatus.RUNNING);
        workflowExecution.setStartedAt(LocalDateTime.now());
        workflowExecutionRepository.save(workflowExecution);
    }

    private TaskExecution createTaskExecution(WorkflowExecution workflowExecution, TaskDefinition taskDefinition) {
        return TaskExecution.builder()
                .workflowExecution(workflowExecution)
                .taskDefinition(taskDefinition)
                .build();
    }
}
