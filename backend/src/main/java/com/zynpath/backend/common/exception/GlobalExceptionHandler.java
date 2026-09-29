package com.zynpath.backend.common.exception;

import com.zynpath.backend.common.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Centralized exception translator ensuring safe, structured JSON error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String requestId = generateRequestId();
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("[{}] Validation failure on {}: {}", requestId, request.getRequestURI(), message);
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                message,
                request.getRequestURI(),
                requestId,
                "VALIDATION",
                false
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(com.zynpath.backend.auth.model.AuthException.class)
    public ResponseEntity<com.zynpath.backend.auth.model.AuthErrorResponse> handleAuthException(
            com.zynpath.backend.auth.model.AuthException ex,
            HttpServletRequest request
    ) {
        log.warn("Auth error on {}: [{}] {}", request.getRequestURI(), ex.getErrorCode(), ex.getMessage());
        com.zynpath.backend.auth.model.AuthErrorResponse error = new com.zynpath.backend.auth.model.AuthErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return ResponseEntity.status(ex.getHttpStatus()).body(error);
    }

    @ExceptionHandler(com.zynpath.backend.security.ratelimit.RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimit(
            com.zynpath.backend.security.ratelimit.RateLimitExceededException ex,
            HttpServletRequest request
    ) {
        String requestId = generateRequestId();
        log.warn("[{}] Rate limit exceeded on {}: {}", requestId, request.getRequestURI(), ex.getMessage());
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                "Too Many Requests",
                ex.getMessage(),
                request.getRequestURI(),
                requestId,
                "NETWORK",
                true
        );
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", String.valueOf(ex.getRetryAfterSeconds()))
                .body(error);
    }

    @ExceptionHandler(com.zynpath.backend.common.resilience.CircuitBreakerOpenException.class)
    public ResponseEntity<ErrorResponse> handleCircuitBreaker(
            com.zynpath.backend.common.resilience.CircuitBreakerOpenException ex,
            HttpServletRequest request
    ) {
        String requestId = generateRequestId();
        log.warn("[{}] Circuit breaker tripped on {}: {}", requestId, request.getRequestURI(), ex.getMessage());
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Service Unavailable",
                "Service temporarily unavailable. Please retry shortly.",
                request.getRequestURI(),
                requestId,
                "NETWORK",
                true
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .header("Retry-After", String.valueOf(Math.max(1, ex.getRetryAfterMs() / 1000)))
                .body(error);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleSecurityException(SecurityException ex, HttpServletRequest request) {
        String requestId = generateRequestId();
        log.warn("[{}] Security authorization denial on {}: {}", requestId, request.getRequestURI(), ex.getMessage());
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                ex.getMessage(),
                request.getRequestURI(),
                requestId,
                "AUTHORIZATION",
                false
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        String requestId = generateRequestId();
        log.warn("[{}] Illegal argument on {}: {}", requestId, request.getRequestURI(), ex.getMessage());
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI(),
                requestId,
                "VALIDATION",
                false
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
        String requestId = generateRequestId();
        log.error("[{}] Unhandled server exception on {}: {}", requestId, request.getRequestURI(), ex.getMessage(), ex);
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected internal error occurred. Please reference request ID: " + requestId,
                request.getRequestURI(),
                requestId,
                "UNKNOWN",
                true
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private String generateRequestId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
