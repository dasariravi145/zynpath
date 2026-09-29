package com.zynpath.backend.auth.model;

/**
 * Authentication claims for online players.
 */
public record AuthClaims(
    String uid,
    String guestUuid,
    String provider,
    boolean authenticated
) {
    public boolean isAuthenticated() {
        return authenticated;
    }

    public String playerId() {
        return uid != null && !uid.isBlank() ? uid : (guestUuid != null ? guestUuid : "");
    }
}
