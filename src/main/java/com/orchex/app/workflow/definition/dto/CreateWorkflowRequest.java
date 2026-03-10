package com.orchex.app.workflow.definition.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class CreateWorkflowRequest {

    @NotBlank(message = "Workflow name must not be empty")
    @Size(max = 100, message = "Workflow name cannot exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Workflow description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Version is required")
    @Min(value = 1, message = "Version must be at least 1")
    private Integer version;

    @NotEmpty(message = "Workflow must contain at least one task")
    @Valid
    private List<TaskDefinitionRequest> tasks;
}
