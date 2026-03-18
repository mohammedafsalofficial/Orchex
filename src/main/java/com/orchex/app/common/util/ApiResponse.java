package com.orchex.app.common.util;

import com.orchex.app.workflow.definition.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
    private ErrorCode errorCode;
    private LocalDateTime timestamp;
    private String path;
}
