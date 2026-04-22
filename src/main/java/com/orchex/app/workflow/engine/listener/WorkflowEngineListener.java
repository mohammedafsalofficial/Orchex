package com.orchex.app.workflow.engine.listener;

import com.orchex.app.workflow.engine.service.TaskExecutionRunner;
import com.orchex.app.workflow.engine.service.WorkflowEngineService;
import com.orchex.app.workflow.execution.event.WorkflowStartedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class WorkflowEngineListener {

    private final WorkflowEngineService workflowEngineService;
    private final TaskExecutionRunner taskExecutionRunner;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleWorkflowStart(WorkflowStartedEvent event) {
        workflowEngineService.executeAsync(event.workflowExecutionId());
        taskExecutionRunner.triggerRunnableTasks(event.workflowExecutionId());
    }
}
