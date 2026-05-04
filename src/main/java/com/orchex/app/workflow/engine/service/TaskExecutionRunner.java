package com.orchex.app.workflow.engine.service;

import com.orchex.app.workflow.execution.model.TaskExecution;
import com.orchex.app.workflow.execution.model.TaskStatus;
import com.orchex.app.workflow.execution.model.WorkflowExecution;
import com.orchex.app.workflow.execution.model.WorkflowStatus;
import com.orchex.app.workflow.execution.repository.TaskExecutionRepository;
import com.orchex.app.workflow.execution.repository.WorkflowExecutionRepository;
import com.orchex.app.workflow.handler.TaskHandlerRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskExecutionRunner {

    private final TaskExecutionRepository taskExecutionRepository;
    private final WorkflowExecutionRepository workflowExecutionRepository;
    private final TaskHandlerRegistry taskHandlerRegistry;

    @Async
    public void executeTaskAsync(UUID taskExecutionId) {
        TaskExecution taskExecution = taskExecutionRepository.findById(taskExecutionId)
                .orElseThrow(() -> new RuntimeException("Task execution not found for id: " + taskExecutionId));
        executeTask(taskExecution);
    }

    public void executeTask(TaskExecution taskExecution) {
        taskExecution.setStatus(TaskStatus.RUNNING);
        taskExecution.setStartedAt(LocalDateTime.now());
        taskExecutionRepository.save(taskExecution);

        try {
            taskHandlerRegistry
                    .getHandler(taskExecution.getTaskDefinition().getTaskType())
                    .execute(taskExecution, taskExecution.getTaskDefinition());
            taskExecution.setStatus(TaskStatus.COMPLETED);
        } catch (Exception ex) {
            handleFailure(taskExecution, ex);
            return;
        }

        taskExecution.setCompletedAt(LocalDateTime.now());
        taskExecutionRepository.save(taskExecution);

        // trigger next tasks via engine (no back-reference needed)
        triggerRunnableTasks(taskExecution.getWorkflowExecution().getId());
        checkWorkflowCompletion(taskExecution.getWorkflowExecution().getId());
    }

    @Transactional
    public void triggerRunnableTasks(UUID workflowExecutionId) {
        List<TaskExecution> taskExecutions = taskExecutionRepository
                .findByWorkflowExecutionId(workflowExecutionId);

        for (TaskExecution taskExecution : taskExecutions) {
            if (taskExecution.getStatus() != TaskStatus.PENDING) continue;

            List<String> dependencies = taskExecution.getTaskDefinition().getDependencies();
            boolean ready = dependencies.isEmpty() ||
                    dependencies.stream().allMatch(dep -> isTaskCompleted(taskExecutions, dep));

            if (ready && readyToRetry(taskExecution)) {
                executeTaskAsync(taskExecution.getId());
            }
        }
    }

    private boolean readyToRetry(TaskExecution taskExecution) {
        return taskExecution.getNextRetryAt() == null || taskExecution.getNextRetryAt().isBefore(LocalDateTime.now());
    }

    private void checkWorkflowCompletion(UUID workflowExecutionId) {
        List<TaskExecution> taskExecutions = taskExecutionRepository
                .findByWorkflowExecutionId(workflowExecutionId);

        boolean allCompleted = taskExecutions.stream()
                .allMatch(t -> t.getStatus() == TaskStatus.COMPLETED);

        if (allCompleted) {
            WorkflowExecution workflowExecution = workflowExecutionRepository
                    .findById(workflowExecutionId)
                    .orElseThrow(() -> new RuntimeException("Workflow execution not found: " + workflowExecutionId));

            workflowExecution.setStatus(WorkflowStatus.COMPLETED);
            workflowExecution.setCompletedAt(LocalDateTime.now());
            workflowExecutionRepository.save(workflowExecution);
        }
    }

    private void handleFailure(TaskExecution taskExecution, Exception ex) {
        int retries = taskExecution.getRetryCount();
        int maxRetries = taskExecution.getTaskDefinition().getRetryLimit() == null ? 0
                : taskExecution.getTaskDefinition().getRetryLimit();

        if (retries < maxRetries) {
            int nextRetry = retries + 1;

            taskExecution.setRetryCount(nextRetry);
            taskExecution.setStatus(TaskStatus.PENDING);

            long delaySeconds = calculateBackoff(nextRetry);

            taskExecution.setNextRetryAt(LocalDateTime.now().plusSeconds(delaySeconds));

            taskExecutionRepository.save(taskExecution);
        } else {
            taskExecution.setStatus(TaskStatus.FAILED);
            taskExecution.setErrorMessage(ex.getMessage());
            taskExecutionRepository.save(taskExecution);

            WorkflowExecution workflowExecution = taskExecution.getWorkflowExecution();
            workflowExecution.setStatus(WorkflowStatus.FAILED);
            workflowExecution.setCompletedAt(LocalDateTime.now());
            workflowExecutionRepository.save(workflowExecution);
        }
    }

    private long calculateBackoff(int retryCount) {
        long delay = (long) (Math.pow(2, retryCount) * 5);
        return Math.min(delay, 300);
    }

    private boolean isTaskCompleted(List<TaskExecution> taskExecutions, String name) {
        return taskExecutions.stream()
                .anyMatch(t -> t.getTaskDefinition().getName().equals(name)
                        && t.getStatus() == TaskStatus.COMPLETED);
    }

    private void simulate(String name) throws InterruptedException {
        System.out.println("Executing: " + name);
        Thread.sleep(1000);
    }

    @Scheduled(fixedDelay = 5000)
    public void retryPendingTasks() {
        taskExecutionRepository
                .findAllByStatusAndNextRetryAtBefore(TaskStatus.PENDING, LocalDateTime.now(), PageRequest.of(0, 50))
                .forEach(task -> triggerRunnableTasks(task.getWorkflowExecution().getId()));
    }
}
