package com.zynpath.backend.common.resilience;

/**
 * Thrown when an external dependency circuit breaker is tripped open to prevent cascading failures.
 *
 * Implements Prompt 38 Section 63.
 */
public class CircuitBreakerOpenException extends RuntimeException {

    private final String serviceName;
    private final long retryAfterMs;

    public CircuitBreakerOpenException(String serviceName, long retryAfterMs) {
        super("Circuit breaker open for service: " + serviceName + ". Retry after " + (retryAfterMs / 1000) + "s.");
        this.serviceName = serviceName;
        this.retryAfterMs = retryAfterMs;
    }

    public String getServiceName() {
        return serviceName;
    }

    public long getRetryAfterMs() {
        return retryAfterMs;
    }
}
