package com.orchex.app.common.util;

import com.orchex.app.workflow.definition.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ResponseUtil {

    public static <T> ApiSuccessResponse<T> success(T data, String message, String path) {
        return ApiSuccessResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }

    public static ApiErrorResponse error(
            List<String> details,
            String message,
            ErrorCode errorCode,
            String path,
            int status
    ) {
        return ApiErrorResponse.builder()
                .success(false)
                .timestamp(LocalDateTime.now())
                .status(status)
                .errorCode(errorCode)
                .message(message)
                .details(details)
                .path(path)
                .traceId(UUID.randomUUID().toString())
                .build();
    }
}
