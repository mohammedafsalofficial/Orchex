package com.orchex.app.workflow.mapper;

import com.orchex.app.workflow.definition.TaskDefinition;
import com.orchex.app.workflow.definition.WorkflowDefinition;
import com.orchex.app.workflow.dto.CreateWorkflowRequest;
import com.orchex.app.workflow.dto.TaskDefinitionRequest;
import com.orchex.app.workflow.dto.WorkflowResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WorkflowMapper {

    WorkflowResponse toResponse(WorkflowDefinition workflow);

    WorkflowDefinition toEntity(CreateWorkflowRequest dto);

    TaskDefinition toTaskEntity(TaskDefinitionRequest dto);

    @AfterMapping
    default void linkTasks(@MappingTarget WorkflowDefinition workflow) {
        if (workflow.getTasks() != null) {
            workflow.getTasks().forEach(task -> task.setWorkflowDefinition(workflow));
        }
    }
}
