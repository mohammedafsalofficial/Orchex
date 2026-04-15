package com.orchex.app.workflow.execution.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StartWorkflowRequest {

    @NotBlank(message = "triggeredBy is required")
    private String triggeredBy;
}
