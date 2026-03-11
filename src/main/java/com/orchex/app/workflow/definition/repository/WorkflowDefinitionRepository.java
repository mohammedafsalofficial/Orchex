package com.orchex.app.workflow.definition.repository;

import com.orchex.app.workflow.definition.model.WorkflowDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkflowDefinitionRepository extends JpaRepository<WorkflowDefinition, UUID> {

    boolean existsByName(String name);
}
