package com.orchex.app.workflow.execution.service;

import com.orchex.app.workflow.definition.exception.WorkflowNotFoundException;
import com.orchex.app.workflow.definition.model.WorkflowDefinition;
import com.orchex.app.workflow.definition.repository.WorkflowDefinitionRepository;
import com.orchex.app.workflow.execution.mapper.WorkflowExecutionMapper;
import com.orchex.app.workflow.execution.model.WorkflowExecution;
import com.orchex.app.workflow.execution.model.WorkflowStatus;
import com.orchex.app.workflow.execution.repository.WorkflowExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowExecutionService {

    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final WorkflowExecutionRepository workflowExecutionRepository;
    private final WorkflowExecutionMapper workflowExecutionMapper;

    public WorkflowExecution startWorkflow(UUID workflowId, String triggeredBy) {
        WorkflowDefinition workflowDefinition = workflowDefinitionRepository.findById(workflowId)
                .orElseThrow(() -> new WorkflowNotFoundException(workflowId));

        WorkflowExecution workflowExecution = workflowExecutionMapper
                .fromDefinition(workflowDefinition);

        workflowExecution.setStatus(WorkflowStatus.PENDING);
        workflowExecution.setTriggeredBy(triggeredBy);
        workflowExecution.setCorrelationId(UUID.randomUUID().toString());

        workflowExecutionRepository.save(workflowExecution);

        return workflowExecution;
    }
}
