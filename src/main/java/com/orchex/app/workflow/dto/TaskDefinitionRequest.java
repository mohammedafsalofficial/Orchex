package com.orchex.app.workflow.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TaskDefinitionRequest {

    @NotBlank(message = "Task name must not be empty")
    @Size(max = 100, message = "Task name cannot exceed 100 characters")
    private String name;

    @NotNull(message = "Step order is required")
    @Min(value = 1, message = "Step order must be >= 1")
    private Integer stepOrder;

    @NotNull(message = "Retry limit is required")
    @Min(value = 0, message = "Retry limit cannot be negative")
    @Max(value = 10, message = "Retry limit cannot exceed 10")
    private Integer retryLimit;

    @NotNull(message = "Timeout is required")
    @Min(value = 1, message = "Timeout must be greater than 0")
    private Integer timeoutSeconds;
}
