package com.orchex.app.workflow.engine.service;

import com.orchex.app.workflow.definition.model.TaskDefinition;
import com.orchex.app.workflow.definition.model.TaskType;
import com.orchex.app.workflow.execution.model.TaskExecution;
import com.orchex.app.workflow.execution.model.TaskStatus;
import com.orchex.app.workflow.execution.model.WorkflowExecution;
import com.orchex.app.workflow.execution.model.WorkflowStatus;
import com.orchex.app.workflow.execution.repository.TaskExecutionRepository;
import com.orchex.app.workflow.execution.repository.WorkflowExecutionRepository;
import com.orchex.app.workflow.handler.TaskHandler;
import com.orchex.app.workflow.handler.TaskHandlerRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskExecutionRunnerTest {

    @Mock
    private TaskExecutionRepository taskExecutionRepository;

    @Mock
    private WorkflowExecutionRepository workflowExecutionRepository;

    @Mock
    private TaskHandlerRegistry taskHandlerRegistry;

    @Mock
    private TaskHandler taskHandler;

    @InjectMocks
    private TaskExecutionRunner taskExecutionRunner;

    @Test
    void shouldNotTriggerRunnableTasksForCancelledWorkflow() {
        UUID workflowExecutionId = UUID.randomUUID();
        WorkflowExecution workflowExecution = WorkflowExecution.builder()
                .id(workflowExecutionId)
                .status(WorkflowStatus.CANCELLED)
                .build();

        when(workflowExecutionRepository.findById(workflowExecutionId)).thenReturn(Optional.of(workflowExecution));

        taskExecutionRunner.triggerRunnableTasks(workflowExecutionId);

        verify(taskExecutionRepository, never()).findByWorkflowExecutionId(workflowExecutionId);
    }

    @Test
    void shouldNotCompleteTaskWhenWorkflowGetsCancelledDuringExecution() {
        UUID workflowExecutionId = UUID.randomUUID();
        WorkflowExecution workflowExecution = WorkflowExecution.builder()
                .id(workflowExecutionId)
                .status(WorkflowStatus.RUNNING)
                .build();
        TaskExecution taskExecution = TaskExecution.builder()
                .id(UUID.randomUUID())
                .workflowExecution(workflowExecution)
                .taskDefinition(taskDefinition())
                .status(TaskStatus.PENDING)
                .build();

        when(taskHandlerRegistry.getHandler(TaskType.HTTP)).thenReturn(taskHandler);
        doAnswer(invocation -> {
            workflowExecution.setStatus(WorkflowStatus.CANCELLED);
            return null;
        }).when(taskHandler).execute(any(TaskExecution.class), any(TaskDefinition.class));

        taskExecutionRunner.executeTask(taskExecution);

        verify(taskExecutionRepository).save(taskExecution);
        verify(taskExecutionRepository, never()).saveAll(any());
        verify(workflowExecutionRepository, never()).save(any(WorkflowExecution.class));
    }

    @Test
    void shouldNotRetryFailedTaskWhenWorkflowIsCancelled() {
        WorkflowExecution workflowExecution = WorkflowExecution.builder()
                .id(UUID.randomUUID())
                .status(WorkflowStatus.RUNNING)
                .build();
        TaskDefinition taskDefinition = taskDefinition();
        taskDefinition.setRetryLimit(2);
        TaskExecution taskExecution = TaskExecution.builder()
                .id(UUID.randomUUID())
                .workflowExecution(workflowExecution)
                .taskDefinition(taskDefinition)
                .status(TaskStatus.PENDING)
                .retryCount(0)
                .build();

        when(taskHandlerRegistry.getHandler(TaskType.HTTP)).thenReturn(taskHandler);
        doAnswer(invocation -> {
            workflowExecution.setStatus(WorkflowStatus.CANCELLED);
            throw new IllegalStateException("boom");
        }).when(taskHandler).execute(any(TaskExecution.class), any(TaskDefinition.class));

        taskExecutionRunner.executeTask(taskExecution);

        verify(taskExecutionRepository).save(taskExecution);
        verify(taskExecutionRepository, never()).findByWorkflowExecutionId(workflowExecution.getId());
        verify(workflowExecutionRepository, never()).save(any(WorkflowExecution.class));
    }

    private TaskDefinition taskDefinition() {
        return TaskDefinition.builder()
                .name("call-api")
                .taskType(TaskType.HTTP)
                .retryLimit(0)
                .build();
    }
}
