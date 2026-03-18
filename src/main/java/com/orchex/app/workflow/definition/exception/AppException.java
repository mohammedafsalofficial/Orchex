package com.orchex.app.workflow.definition.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class AppException extends RuntimeException {

    private final ErrorCode errorCode;
    private final List<String> details;
    private final HttpStatus status;

    public AppException(ErrorCode errorCode, String message, List<String> details, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
        this.status = status;
    }
}
