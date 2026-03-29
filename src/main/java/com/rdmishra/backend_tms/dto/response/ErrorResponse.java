package com.rdmishra.backend_tms.dto.response;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        String error,
        String path,
        HttpStatus status,
        LocalDateTime time) {

}
