package com.orchex.app.workflow.mapper;

import com.orchex.app.workflow.definition.WorkflowDefinition;
import com.orchex.app.workflow.dto.WorkflowResponse;
import org.springframework.stereotype.Component;

@Component
public class WorkflowMapper {

    public WorkflowResponse toResponse(WorkflowDefinition workflow) {
        return WorkflowResponse.builder()
                .id(workflow.getId())
                .name(workflow.getName())
                .description(workflow.getDescription())
                .version(workflow.getVersion())
                .build();
    }
}
