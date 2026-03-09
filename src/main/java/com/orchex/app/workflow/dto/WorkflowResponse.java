package com.orchex.app.workflow.dto;

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
