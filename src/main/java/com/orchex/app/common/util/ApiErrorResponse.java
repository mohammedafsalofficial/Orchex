package com.orchex.app.common.util;

import com.orchex.app.workflow.definition.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ApiErrorResponse {

    private boolean success;
    private LocalDateTime timestamp;
    private int status;
    private ErrorCode errorCode;
    private String message;
    private List<String> details;
    private String path;
    private String traceId;
}
