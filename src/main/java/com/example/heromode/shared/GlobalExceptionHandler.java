package com.example.heromode.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

/**
 * Converts uncaught exceptions into JSON responses with a real message
 * so the frontend (and us, from curl) can see WHY a request failed,
 * instead of a bare 500 with no body.
 *
 * Maps common business errors to appropriate HTTP status codes; everything
 * else stays as 500 but with the exception message exposed.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        String msg = ex.getMessage() == null ? "" : ex.getMessage();
        HttpStatus status = mapStatus(msg);
        log.warn("Handled RuntimeException -> {}: {}", status, msg);
        return ResponseEntity.status(status).body(body(status, msg, ex.getClass().getSimpleName()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAny(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body(HttpStatus.INTERNAL_SERVER_ERROR,
                        ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage(),
                        ex.getClass().getSimpleName()));
    }

    private static HttpStatus mapStatus(String msg) {
        String m = msg.toLowerCase();
        if (m.contains("already exists")) return HttpStatus.CONFLICT;             // 409
        if (m.contains("not found"))      return HttpStatus.NOT_FOUND;            // 404
        if (m.contains("wrong password")) return HttpStatus.UNAUTHORIZED;         // 401
        return HttpStatus.BAD_REQUEST;                                            // 400 fallback for business errors
    }

    private static Map<String, Object> body(HttpStatus status, String message, String exception) {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message,
                "exception", exception
        );
    }
}
