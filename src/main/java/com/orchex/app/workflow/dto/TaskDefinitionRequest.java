package com.orchex.app.workflow.dto;

import lombok.Data;

@Data
public class TaskDefinitionRequest {

    private String name;
    private Integer stepOrder;
    private Integer retryLimit;
    private Integer timeoutSeconds;
}
