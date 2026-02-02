package com.protocolo360.api.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL) // Hides null fields (like message) in JSON
public record ApiResponse<T>(
    T data,
    String message,
    int status
) {
    // Helper static methods for cleaner syntax in controllers
    public static <T> ApiResponse<T> success(T data, String message, int status) {
        return new ApiResponse<>(data, message, status);
    }

    public static <T> ApiResponse<T> error(String message, int status) {
        return new ApiResponse<>(null, message, status);
    }
}
