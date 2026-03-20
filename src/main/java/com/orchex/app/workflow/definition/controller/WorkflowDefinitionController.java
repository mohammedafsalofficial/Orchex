package com.orchex.app.workflow.definition.controller;

import com.orchex.app.common.util.ApiSuccessResponse;
import com.orchex.app.common.util.ResponseUtil;
import com.orchex.app.workflow.definition.model.WorkflowDefinition;
import com.orchex.app.workflow.definition.dto.CreateWorkflowRequest;
import com.orchex.app.workflow.definition.dto.WorkflowResponse;
import com.orchex.app.workflow.definition.mapper.WorkflowMapper;
import com.orchex.app.workflow.definition.service.WorkflowDefinitionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
public class WorkflowDefinitionController {

    private final WorkflowDefinitionService workflowDefinitionService;
    private final WorkflowMapper workflowMapper;

    @PostMapping
    public ResponseEntity<ApiSuccessResponse<WorkflowResponse>> createWorkflow(
            @Valid @RequestBody CreateWorkflowRequest createWorkflowRequest,
            HttpServletRequest httpRequest) {
        WorkflowDefinition createdWorkflow = workflowDefinitionService.createWorkflow(createWorkflowRequest);
        WorkflowResponse response = workflowMapper.toResponse(createdWorkflow);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseUtil.success(response, "Workflow created successfully.", httpRequest.getRequestURI()));
    }

    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<WorkflowResponse>>> getAllWorkflows(HttpServletRequest httpRequest) {
        List<WorkflowDefinition> workflows = workflowDefinitionService.getAllWorkflows();
        List<WorkflowResponse> response = workflows.stream()
                .map(workflowMapper::toResponse)
                .toList();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseUtil.success(response, "Workflows fetched successfully.", httpRequest.getRequestURI()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse<WorkflowResponse>> getWorkflowById(HttpServletRequest request, @PathVariable UUID id) {
        WorkflowDefinition workflow = workflowDefinitionService.getWorkflowById(id);
        WorkflowResponse response = workflowMapper.toResponse(workflow);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseUtil.success(response, "Workflow fetch successfully for id: " + id, request.getRequestURI()));
    }
}
