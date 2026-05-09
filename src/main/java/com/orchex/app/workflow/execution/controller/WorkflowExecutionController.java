package com.orchex.app.workflow.execution.controller;

import com.orchex.app.common.util.ApiSuccessResponse;
import com.orchex.app.common.util.ResponseUtil;
import com.orchex.app.workflow.execution.dto.StartWorkflowRequest;
import com.orchex.app.workflow.execution.dto.WorkflowExecutionResponse;
import com.orchex.app.workflow.execution.dto.WorkflowExecutionStatusResponse;
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
        WorkflowExecution workflowExecution = workflowExecutionService.startWorkflow(
                workflowId,
                requestPayload.getTriggeredBy(),
                requestPayload.getInputPayload()
        );

        WorkflowExecutionResponse workflowExecutionResponse = workflowExecutionMapper.toResponse(workflowExecution);

        ApiSuccessResponse<WorkflowExecutionResponse> responsePayload = ResponseUtil.success(
                workflowExecutionResponse, "Workflow execution started successfully", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responsePayload);
    }

    @GetMapping("/executions/{executionId}")
    public ResponseEntity<ApiSuccessResponse<WorkflowExecutionStatusResponse>> getWorkflowExecution(
            @PathVariable UUID executionId,
            HttpServletRequest request) {
        WorkflowExecution workflowExecution = workflowExecutionService.getWorkflowExecution(executionId);
        WorkflowExecutionStatusResponse workflowExecutionStatusResponse = workflowExecutionMapper.toStatusResponse(workflowExecution);
        ApiSuccessResponse<WorkflowExecutionStatusResponse> responsePayload =
                ResponseUtil.success(workflowExecutionStatusResponse, "Workflow execution fetched successfully", request.getRequestURI());

        return ResponseEntity.ok(responsePayload);
    }

    @PostMapping("/executions/{workflowExecutionId}/cancel")
    public ResponseEntity<ApiSuccessResponse<WorkflowExecutionResponse>> cancelWorkflow(
            @PathVariable UUID workflowExecutionId,
            HttpServletRequest request) {
        WorkflowExecution workflowExecution = workflowExecutionService.cancelWorkflow(workflowExecutionId);

        WorkflowExecutionResponse workflowExecutionResponse = workflowExecutionMapper.toResponse(workflowExecution);
        ApiSuccessResponse<WorkflowExecutionResponse> responsePayload =
                ResponseUtil.success(workflowExecutionResponse, "Workflow execution cancelled successfully", request.getRequestURI());

        return ResponseEntity.ok(responsePayload);
    }
}
