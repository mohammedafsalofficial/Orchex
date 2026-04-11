package com.orchex.app.workflow.definition.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "task_definitions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_definition_id")
    private WorkflowDefinition workflowDefinition;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private TaskType taskType;

    private Integer retryLimit;

    private Integer timeoutSeconds;

    @Column(columnDefinition = "TEXT")
    private String configJson;

    /**
     * Stores dependency task names as a comma-separated string.
     * Empty string means this is a root task (no dependencies).
     */
    @Column(name = "dependencies", columnDefinition = "TEXT")
    private String dependenciesRaw;

    @Transient
    public List<String> getDependencies() {
        if (dependenciesRaw == null || dependenciesRaw.isBlank()) return List.of();
        return List.of(dependenciesRaw.split(","));
    }

    @Transient
    public void setDependencies(List<String> deps) {
        this.dependenciesRaw = (deps == null || deps.isEmpty()) ? "" : String.join(",", deps);
    }
}
