package com.orchex.app.workflow.definition.service;

import com.orchex.app.workflow.definition.dto.CreateWorkflowRequest;
import com.orchex.app.workflow.definition.mapper.WorkflowMapper;
import com.orchex.app.workflow.definition.model.WorkflowDefinition;
import com.orchex.app.workflow.definition.repository.WorkflowDefinitionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkflowDefinitionServiceTest {

    @Mock
    private WorkflowDefinitionRepository workflowDefinitionRepository;

    @Mock
    private WorkflowMapper workflowMapper;

    @Mock
    private WorkflowDefinitionService workflowDefinitionService;

    private static final UUID WORKFLOW_ID = UUID.randomUUID();
    private static final String WORKFLOW_NAME = "Order processing workflow";

    private WorkflowDefinition sampleWorkflow;
    private CreateWorkflowRequest createRequest;

    @BeforeEach
    void setup() {
        sampleWorkflow = new WorkflowDefinition();
        sampleWorkflow.setId(WORKFLOW_ID);
        sampleWorkflow.setName(WORKFLOW_NAME);

        createRequest = new CreateWorkflowRequest();
        createRequest.setName(WORKFLOW_NAME);
    }

    @Nested
    @DisplayName("createWorkflow()")
    class CreateWorkflow {

        @Test
        @DisplayName("saves and returns the workflow when name does not already exist")
        void shouldCreateAndReturnWorkflowWhenNameIsUnique() {
            when(workflowDefinitionRepository.existsByName(WORKFLOW_NAME)).thenReturn(false);
            when(workflowMapper.toEntity(createRequest)).thenReturn(sampleWorkflow);
            when(workflowDefinitionRepository.save(sampleWorkflow)).thenReturn(sampleWorkflow);

            WorkflowDefinition result = workflowDefinitionService.createWorkflow(createRequest);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(WORKFLOW_ID);
            assertThat(result.getName()).isEqualTo(WORKFLOW_NAME);

            verify(workflowDefinitionRepository).existsByName(WORKFLOW_NAME);
            verify(workflowMapper).toEntity(createRequest);
            verify(workflowDefinitionRepository).save(sampleWorkflow);
        }
    }
}
