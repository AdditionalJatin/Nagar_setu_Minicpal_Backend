package com.NagarSetu.Backend.Exceptions;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        String message,
        HttpStatus status,
        int StatusCode
) {}
