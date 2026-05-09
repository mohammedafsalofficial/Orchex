package com.orchex.app.workflow.handler.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orchex.app.workflow.definition.model.TaskDefinition;
import com.orchex.app.workflow.definition.model.TaskType;
import com.orchex.app.workflow.execution.model.TaskExecution;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpTaskHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private HttpServer server;
    private HttpTaskHandler handler;

    @BeforeEach
    void setup() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.start();
        handler = new HttpTaskHandler(objectMapper);
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    void shouldExecuteHttpTaskAndStoreResponsePayload() throws Exception {
        AtomicReference<String> requestBody = new AtomicReference<>();
        server.createContext("/orders", exchange -> {
            requestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            byte[] response = "{\"accepted\":true}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(201, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });

        TaskExecution taskExecution = TaskExecution.builder()
                .inputPayload("{\"orderId\":\"ord-123\"}")
                .build();
        TaskDefinition taskDefinition = httpTask("""
                {
                  "method": "POST",
                  "url": "%s/orders",
                  "headers": { "Content-Type": "application/json" },
                  "body": "{\\"payload\\": {{input}}}",
                  "expectedStatus": 201
                }
                """.formatted(baseUrl()));

        handler.execute(taskExecution, taskDefinition);

        assertThat(requestBody.get()).isEqualTo("{\"payload\": {\"orderId\":\"ord-123\"}}");

        JsonNode output = objectMapper.readTree(taskExecution.getOutputPayload());
        assertThat(output.get("statusCode").asInt()).isEqualTo(201);
        assertThat(output.get("body").asText()).isEqualTo("{\"accepted\":true}");
    }

    @Test
    void shouldFailWhenResponseStatusDoesNotMatchExpectedStatus() {
        server.createContext("/fail", exchange -> {
            byte[] response = "bad request".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(400, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });

        TaskExecution taskExecution = TaskExecution.builder().build();
        TaskDefinition taskDefinition = httpTask("""
                {
                  "method": "GET",
                  "url": "%s/fail",
                  "expectedStatus": 200
                }
                """.formatted(baseUrl()));

        assertThatThrownBy(() -> handler.execute(taskExecution, taskDefinition))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("returned status 400");

        assertThat(taskExecution.getOutputPayload()).contains("\"statusCode\":400");
    }

    @Test
    void shouldUseInputPayloadAsBodyWhenConfigBodyIsMissing() throws Exception {
        AtomicReference<String> requestBody = new AtomicReference<>();
        server.createContext("/echo", exchange -> {
            requestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            byte[] response = "ok".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });

        TaskExecution taskExecution = TaskExecution.builder()
                .inputPayload("raw-body")
                .build();
        TaskDefinition taskDefinition = httpTask("""
                {
                  "method": "POST",
                  "url": "%s/echo"
                }
                """.formatted(baseUrl()));

        handler.execute(taskExecution, taskDefinition);

        assertThat(requestBody.get()).isEqualTo("raw-body");
    }

    private TaskDefinition httpTask(String configJson) {
        return TaskDefinition.builder()
                .name("notify-api")
                .taskType(TaskType.HTTP)
                .timeoutSeconds(5)
                .configJson(configJson)
                .build();
    }

    private String baseUrl() {
        return "http://localhost:" + server.getAddress().getPort();
    }
}
