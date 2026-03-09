package com.orchex.app.workflow.controller;

import com.orchex.app.util.ApiResponse;
import com.orchex.app.util.ResponseUtil;
import com.orchex.app.workflow.definition.WorkflowDefinition;
import com.orchex.app.workflow.dto.CreateWorkflowRequest;
import com.orchex.app.workflow.dto.WorkflowResponse;
import com.orchex.app.workflow.mapper.WorkflowMapper;
import com.orchex.app.workflow.service.WorkflowDefinitionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
    private final WorkflowMapper workflowMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<WorkflowResponse>> createWorkflow(
            @Valid @RequestBody CreateWorkflowRequest createWorkflowRequest,
            HttpServletRequest httpRequest) {
        WorkflowDefinition createdWorkflow = workflowDefinitionService.createWorkflow(createWorkflowRequest);
        WorkflowResponse response = workflowMapper.toResponse(createdWorkflow);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseUtil.success(response, "Workflow created successfully", httpRequest.getRequestURI()));
    }
}
