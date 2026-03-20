package com.orchex.app.workflow.definition.exception;

import com.orchex.app.common.util.ApiErrorResponse;
import com.orchex.app.common.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> details = new ArrayList<>();

        // Field-level errors (@NotBlank, @Size, etc.)
        ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .forEach(details::add);

        // Class-level errors (@ValidWorkflow, etc.)
        ex.getBindingResult()
                .getGlobalErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .forEach(details::add);

        ApiErrorResponse response = ResponseUtil.error(
                details,
                "Validation failed",
                ErrorCode.VALIDATION_ERROR,
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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

    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}
