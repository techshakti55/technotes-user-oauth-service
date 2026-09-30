package com.technotes.auth.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFound(
        UserNotFoundException exception,
        HttpServletRequest request) {

        return buildResponse(
            HttpStatus.NOT_FOUND,
            "USER_NOT_FOUND",
            exception.getMessage(),
            request.getRequestURI()
        );
    }

    @ExceptionHandler(UserDeactivatedException.class)
    public ResponseEntity<ApiErrorResponse> handleUserDeactivated(
        UserDeactivatedException exception,
        HttpServletRequest request) {

        return buildResponse(
            HttpStatus.FORBIDDEN,
            "USER_DEACTIVATED",
            exception.getMessage(),
            request.getRequestURI()
        );
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
        HttpStatus status,
        String code,
        String message,
        String path) {

        ApiErrorResponse response = new ApiErrorResponse(
            Instant.now(),
            status.value(),
            code,
            message,
            path
        );

        return ResponseEntity
            .status(status)
            .body(response);
    }

    public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path
    ) {
    }
}
