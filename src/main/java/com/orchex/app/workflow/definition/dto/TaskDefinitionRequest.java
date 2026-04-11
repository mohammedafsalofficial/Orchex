package com.orchex.app.workflow.definition.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class TaskDefinitionRequest {

    @NotBlank(message = "Task name must not be empty")
    @Size(max = 100, message = "Task name cannot exceed 100 characters")
    private String name;

    @NotNull(message = "Retry limit is required")
    @Min(value = 0, message = "Retry limit cannot be negative")
    @Max(value = 10, message = "Retry limit cannot exceed 10")
    private Integer retryLimit;

    @NotNull(message = "Timeout is required")
    @Min(value = 1, message = "Timeout must be greater than 0")
    @Max(value = 86400, message = "Timeout cannot exceed 24 hours")
    private Integer timeoutSeconds;

    @Size(max = 5000, message = "Config JSON too large")
    private String configJson;

    /**
     * Names of tasks that must complete before this task can run.
     * Empty/null means this is a root task.
     */
    private List<@NotBlank(message = "Dependency task name must not be blank") String> dependencies;
}
