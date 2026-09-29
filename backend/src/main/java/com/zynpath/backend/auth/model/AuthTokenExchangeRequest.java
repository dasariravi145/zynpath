package com.zynpath.backend.auth.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload to exchange a provider identity credential for a Zynpath session.
 *
 * Implements Prompt 18 Section 26.
 */
public record AuthTokenExchangeRequest(
    @NotNull(message = "Provider is required (GOOGLE or FACEBOOK)")
    AuthProvider provider,

    @NotBlank(message = "Provider credential token is required")
    String providerToken,

    String guestUuid
) {}
