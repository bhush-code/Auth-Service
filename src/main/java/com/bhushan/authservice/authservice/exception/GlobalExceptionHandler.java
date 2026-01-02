package com.bhushan.authservice.authservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.websocket.AuthenticationException;
import org.hibernate.exception.AuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import  com.bhushan.authservice.authservice.dto.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {



    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(AuthException exception, HttpServletRequest request)
    {
        return buildResponse(exception,HttpStatus.UNAUTHORIZED,request);
    }

    @ExceptionHandler(CustomUserException.class)
    public ResponseEntity<ErrorResponse> handleCustomUserException(CustomUserException exception, HttpServletRequest request)
    {
        return buildResponse(exception,exception.getStatus(),request);
    }

    @ExceptionHandler(CustomRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleCustomRefreshTokenException(CustomRefreshTokenException exception,HttpServletRequest request)
    {
        return buildResponse(exception,exception.getStatus(),request);
    }

    @ExceptionHandler(CustomRoleException.class)
    public ResponseEntity<ErrorResponse> handleCustomRoleException(CustomRoleException exception, HttpServletRequest request)
    {
        return buildResponse(exception,exception.getStatus(),request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException exception, HttpServletRequest request)
    {
        return buildResponse(exception,HttpStatus.FORBIDDEN,request);
    }

    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception,HttpServletRequest request)
    {
        return buildResponse(exception,HttpStatus.INTERNAL_SERVER_ERROR,request);
    }
    


    private ResponseEntity<ErrorResponse> buildResponse(Exception ex, HttpStatus status, HttpServletRequest request)
    {
        ErrorResponse errorBody=new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()

        );



        return new ResponseEntity<>(errorBody,HttpStatus.valueOf(status.value()));

    }

}
