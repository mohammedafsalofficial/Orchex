package com.orchex.app.workflow.definition.mapper;

import com.orchex.app.workflow.definition.model.TaskDefinition;
import com.orchex.app.workflow.definition.model.WorkflowDefinition;
import com.orchex.app.workflow.definition.dto.CreateWorkflowRequest;
import com.orchex.app.workflow.definition.dto.TaskDefinitionRequest;
import com.orchex.app.workflow.definition.dto.WorkflowResponse;
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
