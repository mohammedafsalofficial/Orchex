package com.orchex.app.workflow.definition.service;

import com.orchex.app.workflow.definition.exception.WorkflowAlreadyExistsException;
import com.orchex.app.workflow.definition.exception.WorkflowNotFoundException;
import com.orchex.app.workflow.definition.model.WorkflowDefinition;
import com.orchex.app.workflow.definition.dto.CreateWorkflowRequest;
import com.orchex.app.workflow.definition.mapper.WorkflowMapper;
import com.orchex.app.workflow.definition.repository.WorkflowDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowDefinitionService {

    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final WorkflowMapper workflowMapper;

    public WorkflowDefinition createWorkflow(CreateWorkflowRequest dto) {
        if (workflowDefinitionRepository.existsByName(dto.getName())) {
            throw new WorkflowAlreadyExistsException(dto.getName());
        }

        WorkflowDefinition workflow = workflowMapper.toEntity(dto);
        return workflowDefinitionRepository.save(workflow);
    }

    public List<WorkflowDefinition> getAllWorkflows() {
        return workflowDefinitionRepository.findAll();
    }

    public WorkflowDefinition getWorkflowById(UUID id) {
        return workflowDefinitionRepository.findById(id)
                .orElseThrow(() -> new WorkflowNotFoundException(id));
    }
}
