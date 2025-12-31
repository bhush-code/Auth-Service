package com.bhushan.authservice.authservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

public class CustomRoleException extends RuntimeException{

    private final HttpStatus status;
    public  CustomRoleException(String message,HttpStatus status){

        super(message);
        this.status=status;
    }

    public HttpStatus getStatus()
    {
        return status;
    }

}
