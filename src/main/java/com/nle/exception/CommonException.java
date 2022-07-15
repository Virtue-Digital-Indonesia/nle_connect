package com.nle.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CommonException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CommonException() {
        super();
    }

    public CommonException(String message) {
        super(message);
    }
}
