package com.example.heromode.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Turns exceptions into a controlled JSON body:
 *
 *   { "timestamp", "status", "error", "code", "message", "errorId", "errors"? }
 *
 * The frontend (App.jsx, Login.jsx) reads response.data.message and the HTTP
 * status, so both keep working exactly as before.
 *
 * What the client may see:
 *   - field validation messages (errors[]), which are about the client's input;
 *   - messages from business errors the app throws on purpose.
 * What the client never sees, in any profile:
 *   - messages of framework/JDK exceptions, the exception class name, stack
 *     traces. Those go to the server log together with errorId, so a report of
 *     "deu erro ERR-1a2b3c4d" can be traced back to the exact log line.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String GENERIC_MESSAGE =
            "Não foi possível concluir a operação. Tente novamente.";

    /**
     * Bean validation on @Valid request bodies. Field messages are written by us
     * and describe the payload the client just sent, so they are safe to return.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<Map<String, String>> fieldErrors = new ArrayList<>();
        for (var fieldError : ex.getBindingResult().getFieldErrors()) {
            Map<String, String> entry = new LinkedHashMap<>();
            entry.put("field", fieldError.getField());
            entry.put("message", fieldError.getDefaultMessage());
            fieldErrors.add(entry);
        }
        for (var globalError : ex.getBindingResult().getGlobalErrors()) {
            Map<String, String> entry = new LinkedHashMap<>();
            entry.put("field", globalError.getObjectName());
            entry.put("message", globalError.getDefaultMessage());
            fieldErrors.add(entry);
        }

        Map<String, Object> body = body(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR",
                "Dados inválidos.", null);
        body.put("errors", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Business errors are thrown as plain {@code new RuntimeException("...")} by
     * the services, with a message meant for the user. Subclasses
     * (DataIntegrityViolationException, JpaSystemException, NullPointerException,
     * ...) come from the framework or the JDK and may carry SQL, constraint names
     * or internal state, so they are treated as internal errors instead.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        if (ex.getClass() != RuntimeException.class) {
            return internalError(ex);
        }

        String message = ex.getMessage() == null ? "" : ex.getMessage();
        HttpStatus status = mapStatus(message);
        log.warn("Business error -> {}: {}", status, message);
        return ResponseEntity.status(status)
                .body(body(status, codeFor(status), message, null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAny(Exception ex) {
        return internalError(ex);
    }

    private ResponseEntity<Map<String, Object>> internalError(Exception ex) {
        String errorId = "ERR-" + UUID.randomUUID().toString().substring(0, 8);
        // Full detail stays here, on the server, never in the response.
        log.error("Unhandled exception [{}]", errorId, ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                        GENERIC_MESSAGE, errorId));
    }

    private static HttpStatus mapStatus(String msg) {
        String m = msg.toLowerCase();
        if (m.contains("already exists")) return HttpStatus.CONFLICT;             // 409
        if (m.contains("not found"))      return HttpStatus.NOT_FOUND;            // 404
        if (m.contains("wrong password")) return HttpStatus.UNAUTHORIZED;         // 401
        return HttpStatus.BAD_REQUEST;                                            // 400 fallback for business errors
    }

    private static String codeFor(HttpStatus status) {
        return switch (status) {
            case CONFLICT -> "CONFLICT";
            case NOT_FOUND -> "NOT_FOUND";
            case UNAUTHORIZED -> "UNAUTHORIZED";
            default -> "BAD_REQUEST";
        };
    }

    private static Map<String, Object> body(HttpStatus status, String code,
                                            String message, String errorId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("code", code);
        body.put("message", message);
        if (errorId != null) {
            body.put("errorId", errorId);
        }
        return body;
    }
}
