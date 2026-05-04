package com.orchex.app.workflow.execution.repository;

import com.orchex.app.workflow.execution.model.TaskExecution;
import com.orchex.app.workflow.execution.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TaskExecutionRepository extends JpaRepository<TaskExecution, UUID> {

    List<TaskExecution> findByWorkflowExecutionId(UUID workflowExecutionId);

    Page<TaskExecution> findAllByStatusAndNestRetryAtBefore(TaskStatus status, LocalDateTime now, Pageable pageable);
}
