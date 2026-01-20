package com.ekufrin.vhsrental.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

public class ApiResponseFactory {
    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(ApiResponse.<T>builder()
                        .status("success")
                        .message(message)
                        .timestamp(Instant.now())
                        .data(data)
                        .build());
    }
}
