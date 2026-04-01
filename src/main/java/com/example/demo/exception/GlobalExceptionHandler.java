package com.example.demo.exception;

import com.example.demo.dto.ErrorDetails;
import lombok.extern.slf4j.Slf4j; // Import SLF4J
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
        // Log the error instead of printStackTrace
        log.error("Global Exception caught: {}", ex.getMessage(), ex);

        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                "An unexpected internal error occurred.",
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}