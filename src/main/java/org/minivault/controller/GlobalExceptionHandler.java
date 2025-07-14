package org.minivault.controller;

import org.minivault.exception.ErrorCodes;
import org.minivault.exception.MiniVaultException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex, WebRequest request) {
        return new ResponseEntity<>(errorBody("Error: " + ex.getMessage(), ErrorCodes.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MiniVaultException.class)
    public ResponseEntity<?> handleAppException(MiniVaultException ex) {
        return new ResponseEntity<>(errorBody(ex), ex.getStatus());
    }

    public static Map<String, Object> errorBody(String message, ErrorCodes errorCode, HttpStatus status) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", status.value(),
                "errorCode", errorCode.getCode(),
                "message", message
        );
    }

    public static Map<String, Object> errorBody(MiniVaultException exception) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", exception.getStatus().value(),
                "errorCode", exception.getErrorCode().getCode(),
                "message", exception.getMessage()
        );
    }
}
