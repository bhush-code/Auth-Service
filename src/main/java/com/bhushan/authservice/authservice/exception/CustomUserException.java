package com.bhushan.authservice.authservice.exception;

import org.springframework.http.HttpStatus;

public class CustomUserException extends RuntimeException {

    private final HttpStatus status;

    public CustomUserException(String message,HttpStatus status)
    {
        super(message);
        this.status=status;

    }

    public HttpStatus getStatus()
    {
        return status;
    }

}
