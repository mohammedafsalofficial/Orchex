package com.orchex.app.workflow.definition.service;

import com.orchex.app.workflow.definition.dto.CreateWorkflowRequest;
import com.orchex.app.workflow.definition.exception.WorkflowAlreadyExistsException;
import com.orchex.app.workflow.definition.exception.WorkflowNotFoundException;
import com.orchex.app.workflow.definition.mapper.WorkflowMapper;
import com.orchex.app.workflow.definition.model.WorkflowDefinition;
import com.orchex.app.workflow.definition.repository.WorkflowDefinitionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkflowDefinitionServiceTest {

    @Mock
    private WorkflowDefinitionRepository workflowDefinitionRepository;

    @Mock
    private WorkflowMapper workflowMapper;

    @InjectMocks
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

    // ─────────────────────────────────────────────────────────────
    // createWorkflow()
    // ─────────────────────────────────────────────────────────────
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

        @Test
        @DisplayName("throws WorkflowAlreadyExistsException when name is already taken")
        void shouldThrowWorkflowAlreadyExistsExceptionWhenNameExists() {
            when(workflowDefinitionRepository.existsByName(WORKFLOW_NAME)).thenReturn(true);

            assertThatThrownBy(() -> workflowDefinitionService.createWorkflow(createRequest))
                    .isInstanceOf(WorkflowAlreadyExistsException.class);

            verify(workflowDefinitionRepository).existsByName(WORKFLOW_NAME);
            verify(workflowMapper, never()).toEntity(any());
            verify(workflowDefinitionRepository, never()).save(any());
        }

        @Test
        @DisplayName("never calls mapper or save when duplicate name is detected")
        void shouldNotPersistAnythingWhenNameIsDuplicate() {
            when(workflowDefinitionRepository.existsByName(WORKFLOW_NAME)).thenReturn(true);

            assertThatThrownBy(() -> workflowDefinitionService.createWorkflow(createRequest))
                    .isInstanceOf(WorkflowAlreadyExistsException.class);

            verifyNoInteractions(workflowMapper);
            verify(workflowDefinitionRepository, never()).save(any());
        }
    }

    // ─────────────────────────────────────────────────────────────
    // getAllWorkflows()
    // ─────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("getAllWorkflows()")
    class GetAllWorkflows {

        @Test
        @DisplayName("returns all workflows from the repository")
        void shouldReturnAllWorkflows() {
            WorkflowDefinition second = new WorkflowDefinition();
            second.setId(UUID.randomUUID());
            second.setName("Invoice workflow");

            when(workflowDefinitionRepository.findAll()).thenReturn(List.of(sampleWorkflow, second));

            List<WorkflowDefinition> result = workflowDefinitionService.getAllWorkflows();

            assertThat(result).hasSize(2);
            assertThat(result).extracting(WorkflowDefinition::getName)
                    .containsExactly(WORKFLOW_NAME, "Invoice workflow");
            verify(workflowDefinitionRepository).findAll();
        }

        @Test
        @DisplayName("returns an empty list when no workflows exist")
        void shouldReturnEmptyListWhenNoWorkflowsExist() {
            when(workflowDefinitionRepository.findAll()).thenReturn(Collections.emptyList());

            List<WorkflowDefinition> result = workflowDefinitionService.getAllWorkflows();

            assertThat(result).isEmpty();
            verify(workflowDefinitionRepository).findAll();
        }

        @Test
        @DisplayName("delegates entirely to the repository with no extra logic")
        void shouldDelegateToRepositoryOnly() {
            when(workflowDefinitionRepository.findAll()).thenReturn(List.of(sampleWorkflow));

            workflowDefinitionService.getAllWorkflows();

            verify(workflowDefinitionRepository, times(1)).findAll();
            verifyNoInteractions(workflowMapper);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // getWorkflowById()
    // ─────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("getWorkflowById()")
    class GetWorkflowById {

        @Test
        @DisplayName("returns the workflow when the ID exists")
        void shouldReturnWorkflowWhenIdExists() {
            when(workflowDefinitionRepository.findById(WORKFLOW_ID))
                    .thenReturn(Optional.of(sampleWorkflow));

            WorkflowDefinition result = workflowDefinitionService.getWorkflowById(WORKFLOW_ID);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(WORKFLOW_ID);
            assertThat(result.getName()).isEqualTo(WORKFLOW_NAME);
            verify(workflowDefinitionRepository).findById(WORKFLOW_ID);
        }

        @Test
        @DisplayName("throws WorkflowNotFoundException when the ID does not exist")
        void shouldThrowWorkflowNotFoundExceptionWhenIdMissing() {
            UUID unknownId = UUID.randomUUID();
            when(workflowDefinitionRepository.findById(unknownId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> workflowDefinitionService.getWorkflowById(unknownId))
                    .isInstanceOf(WorkflowNotFoundException.class);

            verify(workflowDefinitionRepository).findById(unknownId);
        }

        @Test
        @DisplayName("calls findById exactly once and touches no other collaborators")
        void shouldOnlyCallFindById() {
            when(workflowDefinitionRepository.findById(WORKFLOW_ID))
                    .thenReturn(Optional.of(sampleWorkflow));

            workflowDefinitionService.getWorkflowById(WORKFLOW_ID);

            verify(workflowDefinitionRepository, times(1)).findById(WORKFLOW_ID);
            verifyNoInteractions(workflowMapper);
        }
    }
}
