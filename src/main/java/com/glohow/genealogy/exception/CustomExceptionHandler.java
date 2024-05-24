package com.glohow.genealogy.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.ZonedDateTime;
import java.util.Date;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InternalError.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiError> handleInterServerError(InternalError ex){
        return ResponseEntity.internalServerError()
                .body(
                        ApiError.builder()
                                .timestamp(ZonedDateTime.now())
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .errorMessage(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> handleEntityNotFoundError(EntityNotFoundException ex){
        return ResponseEntity.badRequest()
                .body(
                        ApiError.builder()
                                .timestamp(ZonedDateTime.now())
                                .status(HttpStatus.BAD_REQUEST)
                                .errorMessage(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleRuntimeExceptionError(RuntimeException ex){
        return ResponseEntity.badRequest()
                .body(
                        ApiError.builder()
                                .timestamp(ZonedDateTime.now())
                                .status(HttpStatus.BAD_REQUEST)
                                .errorMessage(ex.getMessage())
                                .build()
                );
    }
}
