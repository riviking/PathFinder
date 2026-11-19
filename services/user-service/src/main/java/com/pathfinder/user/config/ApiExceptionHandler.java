package com.pathfinder.user.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    // 409 "email දැනටමත් ඇත", 403 "ගිණුම අක්‍රිය", 404, 401 ... (අපි කේතයේ දාපු පණිවිඩය)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> status(ResponseStatusException e) {
        String message = e.getReason() == null ? "" : e.getReason();
        return ResponseEntity.status(e.getStatusCode())
                .body(Map.of("status", e.getStatusCode().value(), "message", message));
    }

    // 400: form වලංගුතාව (password කෙටියි, email වැරදියි ...)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> invalid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getDefaultMessage())
                .findFirst()
                .orElse("Invalid request.");
        return ResponseEntity.badRequest().body(Map.of("status", 400, "message", message));
    }
}