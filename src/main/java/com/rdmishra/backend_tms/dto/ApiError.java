package com.rdmishra.backend_tms.dto;

import java.time.LocalDateTime;

public record ApiError(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime dateTime) {

    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(status, error, message, path, LocalDateTime.now());
    }
}
