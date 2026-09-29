package com.zynpath.backend.health.model;

/**
 * Structured health response payload for Zynpath backend monitoring.
 * Safe for unauthenticated public polling without leaking secrets or infrastructure internals.
 */
public record HealthResponse(
    String status,
    String service,
    String version,
    long timestamp,
    String environment
) {
    public static HealthResponse healthy(String serviceName, String version, String environment) {
        return new HealthResponse(
            "UP",
            serviceName,
            version,
            System.currentTimeMillis(),
            environment
        );
    }
}
