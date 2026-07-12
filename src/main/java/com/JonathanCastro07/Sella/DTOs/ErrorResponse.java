package com.JonathanCastro07.Sella.DTOs;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        List<String> detalles
) {
    public ErrorResponse(int status, String error, String message) {
        this(LocalDateTime.now(), status, error, message, null);
    }

    public ErrorResponse(int status, String error, String message, List<String> detalles) {
        this(LocalDateTime.now(), status, error, message, detalles);
    }
}
