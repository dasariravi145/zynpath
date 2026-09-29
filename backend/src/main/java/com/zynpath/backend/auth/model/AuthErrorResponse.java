package com.zynpath.backend.auth.model;

/**
 * Standard error response for authentication operations.
 *
 * Implements Prompt 18 Section 27.
 */
public record AuthErrorResponse(
    String errorCode,
    String message,
    long timestamp
) {}
