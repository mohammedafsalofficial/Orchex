package com.orchex.app.workflow.handler.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orchex.app.workflow.definition.model.TaskDefinition;
import com.orchex.app.workflow.definition.model.TaskType;
import com.orchex.app.workflow.execution.model.TaskExecution;
import com.orchex.app.workflow.handler.TaskHandler;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HttpTaskHandler implements TaskHandler {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public TaskType getTaskType() {
        return TaskType.HTTP;
    }

    @Override
    public void execute(TaskExecution taskExecution, TaskDefinition taskDefinition) {
        HttpTaskConfig config = parseConfig(taskDefinition);
        HttpRequest request = buildRequest(taskExecution, taskDefinition, config);
        HttpResponse<String> response = send(request, taskDefinition);

        taskExecution.setOutputPayload(toOutputPayload(response));

        if (!isExpectedStatus(response.statusCode(), config.getExpectedStatus())) {
            throw new IllegalStateException("HTTP task '%s' returned status %d".formatted(
                    taskDefinition.getName(),
                    response.statusCode()
            ));
        }
    }

    private HttpTaskConfig parseConfig(TaskDefinition taskDefinition) {
        if (taskDefinition.getConfigJson() == null || taskDefinition.getConfigJson().isBlank()) {
            throw new IllegalArgumentException("HTTP task '%s' requires configJson".formatted(taskDefinition.getName()));
        }

        try {
            HttpTaskConfig config = objectMapper.readValue(taskDefinition.getConfigJson(), HttpTaskConfig.class);
            if (config.getUrl() == null || config.getUrl().isBlank()) {
                throw new IllegalArgumentException("HTTP task '%s' requires configJson.url".formatted(taskDefinition.getName()));
            }
            return config;
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("HTTP task '%s' has invalid configJson".formatted(taskDefinition.getName()), ex);
        }
    }

    private HttpRequest buildRequest(TaskExecution taskExecution, TaskDefinition taskDefinition, HttpTaskConfig config) {
        String method = config.getMethod() == null || config.getMethod().isBlank()
                ? "GET"
                : config.getMethod().trim().toUpperCase();
        String body = resolveBody(taskExecution, config);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(config.getUrl()))
                .timeout(Duration.ofSeconds(taskDefinition.getTimeoutSeconds() == null ? 30 : taskDefinition.getTimeoutSeconds()));

        if (config.getHeaders() != null) {
            config.getHeaders().forEach(builder::header);
        }

        if (body == null || body.isBlank()) {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        } else {
            builder.method(method, HttpRequest.BodyPublishers.ofString(body));
        }

        return builder.build();
    }

    private String resolveBody(TaskExecution taskExecution, HttpTaskConfig config) {
        if (config.getBody() == null) {
            return taskExecution.getInputPayload();
        }

        String inputPayload = taskExecution.getInputPayload() == null ? "" : taskExecution.getInputPayload();
        return config.getBody().replace("{{input}}", inputPayload);
    }

    private HttpResponse<String> send(HttpRequest request, TaskDefinition taskDefinition) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException ex) {
            throw new IllegalStateException("HTTP task '%s' request failed".formatted(taskDefinition.getName()), ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("HTTP task '%s' was interrupted".formatted(taskDefinition.getName()), ex);
        }
    }

    private String toOutputPayload(HttpResponse<String> response) {
        Map<String, Object> output = new LinkedHashMap<>();
        output.put("statusCode", response.statusCode());
        output.put("body", response.body());
        output.put("headers", response.headers().map());

        try {
            return objectMapper.writeValueAsString(output);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize HTTP task output", ex);
        }
    }

    private boolean isExpectedStatus(int actualStatus, Integer expectedStatus) {
        if (expectedStatus != null) {
            return actualStatus == expectedStatus;
        }

        return actualStatus >= 200 && actualStatus < 300;
    }

    @Data
    private static class HttpTaskConfig {
        private String method;
        private String url;
        private Map<String, String> headers = Map.of();
        private String body;
        private Integer expectedStatus;
    }
}
