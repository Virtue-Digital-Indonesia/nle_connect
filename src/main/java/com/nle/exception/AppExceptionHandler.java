package com.nle.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    public ResponseEntity<Object> handleBadRequestException(WebRequest req, BadRequestException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse apiResponse = new ApiResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getMessage());
        return handleExceptionInternal(ex, apiResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(value = {CommonException.class, ResourceNotFoundException.class})
    @ResponseBody
    public ResponseEntity<Object> handleBusinessException(WebRequest req, ResourceNotFoundException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse apiResponse = new ApiResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ex.getMessage());
        return handleExceptionInternal(ex, apiResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, req);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errorMessage = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
            .collect(Collectors.joining(", "));
        ApiResponse response = new ApiResponse(HttpStatus.BAD_REQUEST, String.format("Validation failed: %s", errorMessage), "");
        return handleExceptionInternal(ex, response, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }


    @ExceptionHandler(DataAccessException.class)
    @ResponseBody
    public ResponseEntity<Object> handleDataAccessException(WebRequest req, DataAccessException ex) {
        logger.error(ex.getMessage(), ex);
        ApiResponse apiResponse = new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex.getMessage());
        return handleExceptionInternal(ex, apiResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, req);
    }
}
