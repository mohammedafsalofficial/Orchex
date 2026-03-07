package com.orchex.app.workflow.definition;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "workflow_definitions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer version;

    private String description;

    private Boolean active;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "workflowDefinition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TaskDefinition> tasks;
}
