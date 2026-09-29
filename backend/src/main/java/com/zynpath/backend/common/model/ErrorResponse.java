package com.zynpath.backend.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Standard RFC 7807-compatible structured error response.
 * Never exposes stack traces or internal environment variables to clients.
 *
 * Implements Prompt 38 Sections 6, 8, 59:
 * - Centralized safe error responses with category classification and retryability indicator.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    int status,
    String error,
    String message,
    String path,
    long timestamp,
    String requestId,
    String category,
    Boolean retryable
) {
    public static ErrorResponse of(int status, String error, String message, String path, String requestId) {
        return new ErrorResponse(status, error, message, path, System.currentTimeMillis(), requestId, "UNKNOWN", false);
    }

    public static ErrorResponse of(int status, String error, String message, String path, String requestId, String category, boolean retryable) {
        return new ErrorResponse(status, error, message, path, System.currentTimeMillis(), requestId, category, retryable);
    }
}
