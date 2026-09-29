package com.zynpath.backend.auth.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload to link a guest account to a third-party provider identity.
 *
 * Implements Prompt 18 Section 20, 25 & 26.
 */
public record AuthLinkRequest(
    @NotNull(message = "Provider is required (GOOGLE or FACEBOOK)")
    AuthProvider provider,

    @NotBlank(message = "Provider credential token is required")
    String providerToken,

    @NotBlank(message = "Local guest UUID is required for linking")
    String guestUuid,

    String displayName
) {}
