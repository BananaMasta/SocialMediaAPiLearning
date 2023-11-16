package com.example.backend1.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ExceptionResponseController extends RuntimeException{
    public ExceptionResponseController() {
    }

    public ExceptionResponseController(String message) {
        super(message);
    }

    public ExceptionResponseController(String message, Throwable cause) {
        super(message, cause);
    }

    public ExceptionResponseController(Throwable cause) {
        super(cause);
    }
}
