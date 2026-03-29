package com.rdmishra.backend_tms.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.rdmishra.backend_tms.dto.ApiError;
import com.rdmishra.backend_tms.dto.response.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandel {

    @ExceptionHandler({
            BadCredentialsException.class,
            UsernameNotFoundException.class,
            AuthenticationException.class
    })
    public ResponseEntity<ApiError> handelAuthenticationError(Exception e, HttpServletRequest request) {
        ApiError api = ApiError.of(HttpStatus.BAD_REQUEST.value(), "Unauthenticate", e.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(api);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handelResourcesNotFoundException(ResourceNotFoundException exception,
            HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), request.getRequestURI(),
                HttpStatus.NOT_FOUND, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handelIllegalArgumentException(IllegalArgumentException exception,
            HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage(), request.getRequestURI(),
                HttpStatus.BAD_REQUEST, LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
