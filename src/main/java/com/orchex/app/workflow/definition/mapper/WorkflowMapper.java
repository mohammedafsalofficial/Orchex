package com.orchex.app.workflow.definition.mapper;

import com.orchex.app.workflow.definition.model.TaskDefinition;
import com.orchex.app.workflow.definition.model.WorkflowDefinition;
import com.orchex.app.workflow.definition.dto.CreateWorkflowRequest;
import com.orchex.app.workflow.definition.dto.TaskDefinitionRequest;
import com.orchex.app.workflow.definition.dto.WorkflowResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WorkflowMapper {

    WorkflowResponse toResponse(WorkflowDefinition workflow);

    WorkflowDefinition toEntity(CreateWorkflowRequest dto);

    @Mapping(target = "dependenciesRaw", source = "dependencies", qualifiedByName = "dependencyListToRaw")
    TaskDefinition toTaskEntity(TaskDefinitionRequest dto);

    @Named("dependencyListToRaw")
    static String dependencyListToRaw(List<String> deps) {
        if (deps == null || deps.isEmpty()) return "";
        return String.join(",", deps);
    }

    @AfterMapping
    default void linkTasks(@MappingTarget WorkflowDefinition workflow) {
        if (workflow.getTasks() != null) {
            workflow.getTasks().forEach(task -> task.setWorkflowDefinition(workflow));
        }
    }
}
