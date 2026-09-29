package com.zynpath.backend.auth.model;

/**
 * Supported external identity providers for Zynpath.
 *
 * Implements Prompt 18 Section 7.
 */
public enum AuthProvider {
    GOOGLE,
    FACEBOOK;

    public static AuthProvider fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Provider cannot be null");
        }
        return AuthProvider.valueOf(value.trim().toUpperCase());
    }
}
