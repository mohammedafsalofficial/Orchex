package com.orchex.app.workflow.execution.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StartWorkflowRequest {

    @NotBlank(message = "triggeredBy is required")
    private String triggeredBy;

    @Size(max = 10000, message = "inputPayload cannot exceed 10000 characters")
    private String inputPayload;
}
