package com.orchex.app.workflow.execution.service;

import com.orchex.app.workflow.definition.exception.WorkflowNotFoundException;
import com.orchex.app.workflow.definition.model.WorkflowDefinition;
import com.orchex.app.workflow.definition.repository.WorkflowDefinitionRepository;
import com.orchex.app.workflow.engine.service.WorkflowEngineService;
import com.orchex.app.workflow.execution.event.WorkflowStartedEvent;
import com.orchex.app.workflow.execution.exception.WorkflowExecutionNotFoundException;
import com.orchex.app.workflow.execution.exception.WorkflowTriggerException;
import com.orchex.app.workflow.execution.mapper.WorkflowExecutionMapper;
import com.orchex.app.workflow.execution.model.TaskExecution;
import com.orchex.app.workflow.execution.model.TaskStatus;
import com.orchex.app.workflow.execution.model.WorkflowExecution;
import com.orchex.app.workflow.execution.model.WorkflowStatus;
import com.orchex.app.workflow.execution.repository.TaskExecutionRepository;
import com.orchex.app.workflow.execution.repository.WorkflowExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowExecutionService {

    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final WorkflowExecutionRepository workflowExecutionRepository;
    private final WorkflowExecutionMapper workflowExecutionMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final TaskExecutionRepository taskExecutionRepository;

    @Transactional
    public WorkflowExecution startWorkflow(UUID workflowId, String triggeredBy, String inputPayload) {
        WorkflowDefinition workflowDefinition = workflowDefinitionRepository.findById(workflowId)
                .orElseThrow(() -> new WorkflowNotFoundException(workflowId));

        if (workflowDefinition.getTasks().isEmpty())
            throw new WorkflowTriggerException(workflowId, "Workflow has no tasks defined");

        WorkflowExecution workflowExecution = workflowExecutionMapper
                .fromDefinition(workflowDefinition);

        workflowExecution.setStatus(WorkflowStatus.PENDING);
        workflowExecution.setTriggeredBy(triggeredBy);
        workflowExecution.setInputPayload(inputPayload);
        workflowExecution.setCorrelationId(UUID.randomUUID().toString());

        workflowExecutionRepository.save(workflowExecution);

        applicationEventPublisher.publishEvent(new WorkflowStartedEvent(workflowExecution.getId()));

        return workflowExecution;
    }

    public WorkflowExecution getWorkflowExecution(UUID executionId) {
        return workflowExecutionRepository.findById(executionId)
                .orElseThrow(() -> new WorkflowExecutionNotFoundException(executionId));
    }

    public WorkflowExecution cancelWorkflow(UUID workflowExecutionId) {
        WorkflowExecution workflowExecution = workflowExecutionRepository.findById(workflowExecutionId)
                .orElseThrow(() -> new WorkflowExecutionNotFoundException(workflowExecutionId));

        if (workflowExecution.getStatus() == WorkflowStatus.COMPLETED ||
                workflowExecution.getStatus() == WorkflowStatus.CANCELLED ||
                workflowExecution.getStatus() == WorkflowStatus.FAILED ||
                workflowExecution.getStatus() == WorkflowStatus.TIMED_OUT) {
            throw new IllegalStateException("Cannot cancel workflow in state: " + workflowExecution.getStatus());
        }

        workflowExecution.setStatus(WorkflowStatus.CANCELLED);
        workflowExecution.setCompletedAt(LocalDateTime.now());

        List<TaskExecution> taskExecutions = taskExecutionRepository.findByWorkflowExecutionId(workflowExecutionId);

        for (TaskExecution taskExecution : taskExecutions) {
            if (taskExecution.getStatus() == TaskStatus.RUNNING || taskExecution.getStatus() == TaskStatus.PENDING) {
                taskExecution.setStatus(TaskStatus.CANCELLED);
            }
        }

        taskExecutionRepository.saveAll(taskExecutions);
        return workflowExecutionRepository.save(workflowExecution);
    }
}
