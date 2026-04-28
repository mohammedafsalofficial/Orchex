package com.orchex.app.workflow.execution.service;

import com.orchex.app.workflow.definition.exception.WorkflowNotFoundException;
import com.orchex.app.workflow.definition.model.WorkflowDefinition;
import com.orchex.app.workflow.definition.repository.WorkflowDefinitionRepository;
import com.orchex.app.workflow.engine.service.WorkflowEngineService;
import com.orchex.app.workflow.execution.event.WorkflowStartedEvent;
import com.orchex.app.workflow.execution.exception.WorkflowExecutionNotFoundException;
import com.orchex.app.workflow.execution.mapper.WorkflowExecutionMapper;
import com.orchex.app.workflow.execution.model.WorkflowExecution;
import com.orchex.app.workflow.execution.model.WorkflowStatus;
import com.orchex.app.workflow.execution.repository.WorkflowExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowExecutionService {

    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final WorkflowExecutionRepository workflowExecutionRepository;
    private final WorkflowExecutionMapper workflowExecutionMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public WorkflowExecution startWorkflow(UUID workflowId, String triggeredBy) {
        WorkflowDefinition workflowDefinition = workflowDefinitionRepository.findById(workflowId)
                .orElseThrow(() -> new WorkflowNotFoundException(workflowId));

        WorkflowExecution workflowExecution = workflowExecutionMapper
                .fromDefinition(workflowDefinition);

        workflowExecution.setStatus(WorkflowStatus.PENDING);
        workflowExecution.setTriggeredBy(triggeredBy);
        workflowExecution.setCorrelationId(UUID.randomUUID().toString());

        workflowExecutionRepository.save(workflowExecution);

        applicationEventPublisher.publishEvent(new WorkflowStartedEvent(workflowExecution.getId()));

        return workflowExecution;
    }

    public WorkflowExecution getWorkflowExecution(UUID executionId) {
        return workflowExecutionRepository.findById(executionId)
                .orElseThrow(() -> new WorkflowExecutionNotFoundException(executionId));
    }
}
