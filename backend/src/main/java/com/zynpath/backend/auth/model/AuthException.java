package com.zynpath.backend.auth.model;

import org.springframework.http.HttpStatus;

/**
 * Domain exception for authentication and identity operations.
 *
 * Implements Prompt 18 Section 27.
 */
public class AuthException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    public AuthException(String errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public AuthException(String errorCode, String message) {
        this(errorCode, message, HttpStatus.BAD_REQUEST);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public static AuthException providerUnavailable(String message) {
        return new AuthException("PROVIDER_UNAVAILABLE", message, HttpStatus.SERVICE_UNAVAILABLE);
    }

    public static AuthException invalidCredential(String message) {
        return new AuthException("INVALID_PROVIDER_CREDENTIAL", message, HttpStatus.UNAUTHORIZED);
    }

    public static AuthException expiredCredential(String message) {
        return new AuthException("EXPIRED_CREDENTIAL", message, HttpStatus.UNAUTHORIZED);
    }

    public static AuthException accountLinkConflict(String message) {
        return new AuthException("ACCOUNT_LINK_CONFLICT", message, HttpStatus.CONFLICT);
    }

    public static AuthException sessionExpired(String message) {
        return new AuthException("SESSION_EXPIRED", message, HttpStatus.UNAUTHORIZED);
    }

    public static AuthException badRequest(String message) {
        return new AuthException("BAD_REQUEST", message, HttpStatus.BAD_REQUEST);
    }

    public static AuthException unauthenticated(String message) {
        return new AuthException("UNAUTHENTICATED", message, HttpStatus.UNAUTHORIZED);
    }

    public static AuthException forbidden(String message) {
        return new AuthException("FORBIDDEN", message, HttpStatus.FORBIDDEN);
    }

    public static AuthException notFound(String message) {
        return new AuthException("NOT_FOUND", message, HttpStatus.NOT_FOUND);
    }
}
