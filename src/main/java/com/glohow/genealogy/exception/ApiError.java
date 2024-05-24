package com.glohow.genealogy.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@Builder
public class ApiError {
    private ZonedDateTime timestamp;
    private HttpStatus status;
    private Integer statusCode;
    private String errorMessage;
}
