package com.orchex.app.workflow.execution.repository;

import com.orchex.app.workflow.execution.model.TaskExecution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskExecutionRepository extends JpaRepository<TaskExecution, UUID> {
}
