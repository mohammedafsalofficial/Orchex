package com.orchex.app.workflow.service;

import com.orchex.app.workflow.definition.TaskDefinition;
import com.orchex.app.workflow.definition.WorkflowDefinition;
import com.orchex.app.workflow.dto.CreateWorkflowRequest;
import com.orchex.app.workflow.mapper.WorkflowMapper;
import com.orchex.app.workflow.repository.WorkflowDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkflowDefinitionService {

    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final WorkflowMapper workflowMapper;

    public WorkflowDefinition createWorkflow(CreateWorkflowRequest dto) {
        WorkflowDefinition workflow = workflowMapper.toEntity(dto);
        return workflowDefinitionRepository.save(workflow);
    }

    public List<WorkflowDefinition> getAllWorkflows() {
        return workflowDefinitionRepository.findAll();
    }
}
