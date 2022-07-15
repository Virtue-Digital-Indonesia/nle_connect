package com.nle.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class ApiResponse {
    private HttpStatus status;
    private String message;
    private List<String> errors;

    public ApiResponse(HttpStatus status, String message, List<String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public ApiResponse(HttpStatus status, String message, String error) {
        this.status = status;
        this.message = message;
        errors = Arrays.asList(error);
    }
}
