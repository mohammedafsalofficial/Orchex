package com.orchex.app.workflow.execution.repository;

import com.orchex.app.workflow.execution.model.WorkflowExecution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkflowExecutionRepository extends JpaRepository<WorkflowExecution, UUID> {
}
