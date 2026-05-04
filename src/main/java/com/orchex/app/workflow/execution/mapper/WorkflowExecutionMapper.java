package com.orchex.app.workflow.execution.mapper;

import com.orchex.app.workflow.definition.model.WorkflowDefinition;
import com.orchex.app.workflow.execution.dto.TaskExecutionResponse;
import com.orchex.app.workflow.execution.dto.WorkflowExecutionResponse;
import com.orchex.app.workflow.execution.dto.WorkflowExecutionStatusResponse;
import com.orchex.app.workflow.execution.model.TaskExecution;
import com.orchex.app.workflow.execution.model.TaskStatus;
import com.orchex.app.workflow.execution.model.WorkflowExecution;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WorkflowExecutionMapper {

    @Mapping(target = "workflowDefinitionId", source = "workflowDefinition.id")
    WorkflowExecutionResponse toResponse(WorkflowExecution execution);

    @Mapping(target = "workflowDefinitionId", source = "workflowDefinition.id")
    @Mapping(target = "tasks", source = "taskExecutions")
    @Mapping(target = "totalTasks", expression = "java(countTotal(execution))")
    @Mapping(target = "completedTasks", expression = "java(countCompleted(execution))")
    @Mapping(target = "failedTasks", expression = "java(countFailed(execution))")
    WorkflowExecutionStatusResponse toStatusResponse(WorkflowExecution execution);

    @Mapping(target = "name", source = "taskDefinition.name")
    TaskExecutionResponse toTaskResponse(TaskExecution taskExecution);

    List<TaskExecutionResponse> toTaskResponseList(List<TaskExecution> taskExecutions);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "correlationId", ignore = true)
    @Mapping(target = "triggeredBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "startedAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "errorMessage", ignore = true)
    @Mapping(target = "inputPayload", ignore = true)
    @Mapping(target = "taskExecutions", ignore = true)
    @Mapping(target = "workflowDefinition", source = "definition")
    WorkflowExecution fromDefinition(WorkflowDefinition definition);

    default int countTotal(WorkflowExecution workflowExecution) {
        return workflowExecution.getTaskExecutions() != null ? workflowExecution.getTaskExecutions().size() : 0;
    }

    default int countCompleted(WorkflowExecution workflowExecution) {
        return countByStatus(workflowExecution, TaskStatus.COMPLETED);
    }

    default int countFailed(WorkflowExecution workflowExecution) {
        return countByStatus(workflowExecution, TaskStatus.FAILED);
    }

    default int countByStatus(WorkflowExecution workflowExecution, TaskStatus taskStatus) {
        if (workflowExecution.getTaskExecutions() == null) return 0;

        return (int) workflowExecution.getTaskExecutions().stream()
                .filter(t -> t.getStatus() == taskStatus)
                .count();
    }
}
