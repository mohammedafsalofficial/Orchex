package com.orchex.app.workflow.controller;

import com.orchex.app.workflow.definition.WorkflowDefinition;
import com.orchex.app.workflow.dto.CreateWorkflowRequest;
import com.orchex.app.workflow.dto.WorkflowResponse;
import com.orchex.app.workflow.service.WorkflowDefinitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
public class WorkflowDefinitionController {

    private final WorkflowDefinitionService workflowDefinitionService;

    @PostMapping
    public ResponseEntity<WorkflowResponse> createWorkflow(@RequestBody CreateWorkflowRequest request) {
        WorkflowDefinition createdWorkflow = workflowDefinitionService.createWorkflow(request);
        WorkflowResponse response = WorkflowResponse.builder()
                .id(createdWorkflow.getId())
                .name(createdWorkflow.getName())
                .description(createdWorkflow.getDescription())
                .version(createdWorkflow.getVersion())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
