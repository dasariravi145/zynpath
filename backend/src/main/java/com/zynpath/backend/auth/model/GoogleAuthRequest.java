package com.zynpath.backend.auth.model;

import jakarta.validation.constraints.NotBlank;

/**
 * Direct request payload for Google Credential Manager authentication.
 */
public record GoogleAuthRequest(
    @NotBlank(message = "Google ID token is required")
    String idToken,

    String guestUuid
) {}
