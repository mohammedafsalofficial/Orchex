package com.orchex.app.workflow.execution.service;

import com.orchex.app.workflow.definition.exception.WorkflowNotFoundException;
import com.orchex.app.workflow.definition.model.WorkflowDefinition;
import com.orchex.app.workflow.definition.repository.WorkflowDefinitionRepository;
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

    public WorkflowExecution startWorkflow(UUID workflowId, String triggeredBy) {
        WorkflowDefinition workflowDefinition = workflowDefinitionRepository.findById(workflowId)
                .orElseThrow(() -> new WorkflowNotFoundException(workflowId));

        WorkflowExecution workflowExecution = WorkflowExecution.builder()
                .workflowDefinition(workflowDefinition)
                .status(WorkflowStatus.PENDING)
                .correlationId(UUID.randomUUID().toString())
                .triggeredBy(triggeredBy)
                .build();

        workflowExecutionRepository.save(workflowExecution);

        return workflowExecution;
    }
}
