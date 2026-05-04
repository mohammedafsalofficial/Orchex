package com.orchex.app.workflow.engine.service;

import com.orchex.app.workflow.definition.model.TaskDefinition;
import com.orchex.app.workflow.definition.model.TaskType;
import com.orchex.app.workflow.definition.model.WorkflowDefinition;
import com.orchex.app.workflow.execution.model.TaskExecution;
import com.orchex.app.workflow.execution.model.WorkflowExecution;
import com.orchex.app.workflow.execution.model.WorkflowStatus;
import com.orchex.app.workflow.execution.repository.TaskExecutionRepository;
import com.orchex.app.workflow.execution.repository.WorkflowExecutionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkflowEngineServiceTest {

    @Mock
    private WorkflowExecutionRepository workflowExecutionRepository;

    @Mock
    private TaskExecutionRepository taskExecutionRepository;

    @Mock
    private TaskExecutionRunner taskExecutionRunner;

    @InjectMocks
    private WorkflowEngineService workflowEngineService;

    @Test
    void shouldCopyWorkflowInputPayloadOnlyToRootTasks() {
        UUID executionId = UUID.randomUUID();
        TaskDefinition rootTask = task("fetch-order", List.of());
        TaskDefinition dependentTask = task("notify-customer", List.of("fetch-order"));
        WorkflowDefinition workflowDefinition = WorkflowDefinition.builder()
                .tasks(List.of(rootTask, dependentTask))
                .build();
        WorkflowExecution workflowExecution = WorkflowExecution.builder()
                .id(executionId)
                .workflowDefinition(workflowDefinition)
                .inputPayload("{\"orderId\":\"ord-123\"}")
                .build();

        when(workflowExecutionRepository.findById(executionId)).thenReturn(Optional.of(workflowExecution));

        workflowEngineService.executeAsync(executionId);

        ArgumentCaptor<List<TaskExecution>> captor = ArgumentCaptor.forClass(List.class);
        verify(taskExecutionRepository).saveAll(captor.capture());

        List<TaskExecution> taskExecutions = captor.getValue();
        assertThat(taskExecutions).hasSize(2);
        assertThat(taskExecutions)
                .filteredOn(task -> task.getTaskDefinition().getName().equals("fetch-order"))
                .singleElement()
                .extracting(TaskExecution::getInputPayload)
                .isEqualTo("{\"orderId\":\"ord-123\"}");
        assertThat(taskExecutions)
                .filteredOn(task -> task.getTaskDefinition().getName().equals("notify-customer"))
                .singleElement()
                .extracting(TaskExecution::getInputPayload)
                .isNull();
        assertThat(workflowExecution.getStatus()).isEqualTo(WorkflowStatus.RUNNING);
    }

    private TaskDefinition task(String name, List<String> dependencies) {
        TaskDefinition taskDefinition = TaskDefinition.builder()
                .name(name)
                .taskType(TaskType.HTTP)
                .build();
        taskDefinition.setDependencies(dependencies);
        return taskDefinition;
    }
}
