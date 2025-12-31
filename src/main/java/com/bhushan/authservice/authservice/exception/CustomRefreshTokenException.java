package com.bhushan.authservice.authservice.exception;


import org.springframework.http.HttpStatus;

public class CustomRefreshTokenException extends RuntimeException {

    private final HttpStatus status;
    public CustomRefreshTokenException(String message, HttpStatus status)
    {
        super(message);
        this.status=status;
    }

    public HttpStatus getStatus()
    {
        return  status;
    }

}
