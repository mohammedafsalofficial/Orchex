package com.orchex.app.workflow.repository;

import com.orchex.app.workflow.definition.WorkflowDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkflowDefinitionRepository extends JpaRepository<WorkflowDefinition, UUID> {
}
