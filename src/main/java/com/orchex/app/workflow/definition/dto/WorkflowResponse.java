package com.orchex.app.workflow.definition.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record WorkflowResponse(
        UUID id,
        String name,
        String description,
        Integer version
) {
}
