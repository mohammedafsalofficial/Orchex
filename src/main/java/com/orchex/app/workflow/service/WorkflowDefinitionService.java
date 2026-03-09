package com.orchex.app.workflow.service;

import com.orchex.app.workflow.definition.TaskDefinition;
import com.orchex.app.workflow.definition.WorkflowDefinition;
import com.orchex.app.workflow.dto.CreateWorkflowRequest;
import com.orchex.app.workflow.repository.WorkflowDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkflowDefinitionService {

    private final WorkflowDefinitionRepository workflowDefinitionRepository;

    public WorkflowDefinition createWorkflow(CreateWorkflowRequest request) {
        WorkflowDefinition workflow = WorkflowDefinition.builder()
                .name(request.getName())
                .description(request.getDescription())
                .version(request.getVersion())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        List<TaskDefinition> tasks = request.getTasks()
                .stream()
                .map(task -> TaskDefinition.builder()
                        .name(task.getName())
                        .stepOrder(task.getStepOrder())
                        .retryLimit(task.getRetryLimit())
                        .timeoutSeconds(task.getTimeoutSeconds())
                        .workflowDefinition(workflow)
                        .build()
                ).toList();

        workflow.setTasks(tasks);

        return workflowDefinitionRepository.save(workflow);
    }
}
