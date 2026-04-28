package com.orchex.app.workflow.definition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orchex.app.workflow.definition.dto.CreateWorkflowRequest;
import com.orchex.app.workflow.definition.dto.TaskDefinitionRequest;
import com.orchex.app.workflow.definition.model.TaskType;
import com.orchex.app.workflow.definition.model.WorkflowDefinition;
import com.orchex.app.workflow.definition.repository.WorkflowDefinitionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class WorkflowDefinitionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WorkflowDefinitionRepository workflowDefinitionRepository;

    @BeforeEach
    void setup() {
        workflowDefinitionRepository.deleteAll();
    }

    @Test
    void shouldCreateWorkflowSuccessfully() throws Exception {
        CreateWorkflowRequest request = new CreateWorkflowRequest();
        request.setName("test-workflow");
        request.setDescription("test description");
        request.setVersion(1);

        TaskDefinitionRequest task = new TaskDefinitionRequest();
        task.setName("task1");
        task.setTaskType(TaskType.HTTP);
        task.setRetryLimit(3);
        task.setTimeoutSeconds(60);
        task.setDependencies(List.of());

        request.setTasks(List.of(task));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/workflows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value("test-workflow"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.version").value(1));

        Assertions.assertEquals(1, workflowDefinitionRepository.count());
    }

    @Test
    void shouldFailWhenNameIsMissing() throws Exception {
        CreateWorkflowRequest request = new CreateWorkflowRequest();
        request.setVersion(1);
        request.setTasks(List.of());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/workflows")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void shouldFailWhenWorkflowAlreadyExists() throws Exception {
        WorkflowDefinition existingWorkflowDefinition = WorkflowDefinition.builder()
                .name("duplicate-workflow")
                .version(1)
                .build();

        workflowDefinitionRepository.save(existingWorkflowDefinition);

        CreateWorkflowRequest request = new CreateWorkflowRequest();
        request.setName("duplicate-workflow");
        request.setVersion(1);

        TaskDefinitionRequest task = new TaskDefinitionRequest();
        task.setName("task1");
        task.setTaskType(TaskType.HTTP);
        task.setRetryLimit(1);
        task.setTimeoutSeconds(10);
        task.setDependencies(List.of());

        request.setTasks(List.of(task));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/workflows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }
}
