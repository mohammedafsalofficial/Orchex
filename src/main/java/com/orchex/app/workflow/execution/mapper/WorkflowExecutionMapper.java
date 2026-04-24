package com.orchex.app.workflow.execution.mapper;

import com.orchex.app.workflow.definition.model.WorkflowDefinition;
import com.orchex.app.workflow.execution.dto.WorkflowExecutionResponse;
import com.orchex.app.workflow.execution.model.WorkflowExecution;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkflowExecutionMapper {

    @Mapping(target = "workflowDefinitionId", source = "workflowDefinition.id")
    WorkflowExecutionResponse toResponse(WorkflowExecution execution);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "correlationId", ignore = true)
    @Mapping(target = "triggeredBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "startedAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "errorMessage", ignore = true)
    @Mapping(target = "taskExecutions", ignore = true)
    @Mapping(target = "workflowDefinition", source = "definition")
    WorkflowExecution fromDefinition(WorkflowDefinition definition);
}
