package com.orchex.app.common.util;

import com.orchex.app.workflow.definition.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class ResponseUtil {

    public static <T> ApiResponse<T> success(T data, String message, String path) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .errors(null)
                .errorCode(null)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }

    public static <T> ApiResponse<T> error(List<String> errors, String message, ErrorCode errorCode, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .errors(errors)
                .errorCode(errorCode)
                .timestamp(LocalDateTime.now())
                .path(path + "/error")
                .build();
    }

    public static <T> ApiResponse<T> error(String error, String message, ErrorCode errorCode, String path) {
        return error(Collections.singletonList(error), message, errorCode, path);
    }
}
