package com.orchex.app.workflow.execution.controller;

import com.orchex.app.common.util.ApiSuccessResponse;
import com.orchex.app.common.util.ResponseUtil;
import com.orchex.app.workflow.execution.dto.StartWorkflowRequest;
import com.orchex.app.workflow.execution.dto.WorkflowExecutionResponse;
import com.orchex.app.workflow.execution.mapper.WorkflowExecutionMapper;
import com.orchex.app.workflow.execution.model.WorkflowExecution;
import com.orchex.app.workflow.execution.service.WorkflowExecutionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
public class WorkflowExecutionController {

    private final WorkflowExecutionService workflowExecutionService;
    private final WorkflowExecutionMapper workflowExecutionMapper;

    @PostMapping("/{workflowId}/execute")
    public ResponseEntity<ApiSuccessResponse<WorkflowExecutionResponse>> startWorkflow(
            @PathVariable UUID workflowId,
            @Valid @RequestBody StartWorkflowRequest requestPayload,
            HttpServletRequest request) {
        WorkflowExecution workflowExecution = workflowExecutionService.startWorkflow(workflowId, requestPayload.getTriggeredBy());

        WorkflowExecutionResponse workflowExecutionResponse = workflowExecutionMapper.toResponse(workflowExecution);

        ApiSuccessResponse<WorkflowExecutionResponse> responsePayload = ResponseUtil.success(
                workflowExecutionResponse, "Workflow execution started successfully", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CREATED).body(responsePayload);
    }
}
