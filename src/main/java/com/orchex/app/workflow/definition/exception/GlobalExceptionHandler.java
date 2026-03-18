package com.orchex.app.workflow.definition.exception;

import com.orchex.app.common.util.ApiErrorResponse;
import com.orchex.app.common.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiErrorResponse> handleAppException(AppException ex, HttpServletRequest request) {
        ApiErrorResponse response = ResponseUtil.error(
                ex.getDetails(),
                ex.getMessage(),
                ex.getErrorCode(),
                request.getRequestURI(),
                ex.getStatus().value()
        );
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred", ex);

        ApiErrorResponse response = ResponseUtil.error(
                List.of("An unexpected error occurred. Please contact support."),
                "Internal Server Error",
                ErrorCode.INTERNAL_SERVER_ERROR,
                request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
