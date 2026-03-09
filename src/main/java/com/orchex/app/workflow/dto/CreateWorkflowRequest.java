package com.orchex.app.workflow.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateWorkflowRequest {

    private String name;
    private String description;
    private Integer version;
    private List<TaskDefinitionRequest> tasks;
}
