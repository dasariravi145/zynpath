package com.zynpath.backend.common.resilience;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Lightweight, in-memory circuit breaker and timeout orchestrator for external dependencies.
 *
 * Implements Prompt 38 Sections 62, 63, 64:
 * - Protects against cascading failures from auth providers, Google Play billing, push notifications, and ads.
 * - Enforces bounded failure thresholds (5 consecutive failures) and fast-fail backoff (30 seconds).
 * - Avoids distributed infrastructure overhead while ensuring resilience.
 */
@Service
public class CircuitBreakerService {

    private static final Logger log = LoggerFactory.getLogger(CircuitBreakerService.class);

    private static final int DEFAULT_FAILURE_THRESHOLD = 5;
    private static final long DEFAULT_RESET_TIMEOUT_MS = 30_000L; // 30 seconds

    public enum State {
        CLOSED,
        OPEN,
        HALF_OPEN
    }

    private static class CircuitState {
        State state = State.CLOSED;
        int failureCount = 0;
        long lastFailureTimestamp = 0L;
    }

    private final Map<String, CircuitState> circuits = new ConcurrentHashMap<>();

    /**
     * Executes an external operation protected by a circuit breaker and timeout.
     */
    public <T> T executeWithProtection(
            String serviceName,
            Duration timeout,
            Callable<T> operation,
            Callable<T> fallback
    ) {
        checkCircuitState(serviceName);

        CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> {
            try {
                return operation.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        try {
            T result = future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
            recordSuccess(serviceName);
            return result;
        } catch (TimeoutException e) {
            future.cancel(true);
            recordFailure(serviceName, "Timeout after " + timeout.toMillis() + "ms");
            return executeFallback(serviceName, fallback, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            recordFailure(serviceName, "Execution interrupted");
            return executeFallback(serviceName, fallback, e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            recordFailure(serviceName, cause.getMessage());
            return executeFallback(serviceName, fallback, cause);
        }
    }

    private synchronized void checkCircuitState(String serviceName) {
        CircuitState state = circuits.computeIfAbsent(serviceName, k -> new CircuitState());
        long now = System.currentTimeMillis();

        if (state.state == State.OPEN) {
            if (now - state.lastFailureTimestamp > DEFAULT_RESET_TIMEOUT_MS) {
                log.info("Circuit breaker for '{}' transitioning from OPEN to HALF_OPEN probe", serviceName);
                state.state = State.HALF_OPEN;
            } else {
                long remainingMs = DEFAULT_RESET_TIMEOUT_MS - (now - state.lastFailureTimestamp);
                throw new CircuitBreakerOpenException(serviceName, remainingMs);
            }
        }
    }

    private synchronized void recordSuccess(String serviceName) {
        CircuitState state = circuits.get(serviceName);
        if (state != null) {
            if (state.state != State.CLOSED) {
                log.info("Circuit breaker for '{}' restored to CLOSED state", serviceName);
            }
            state.state = State.CLOSED;
            state.failureCount = 0;
        }
    }

    private synchronized void recordFailure(String serviceName, String reason) {
        CircuitState state = circuits.computeIfAbsent(serviceName, k -> new CircuitState());
        state.failureCount++;
        state.lastFailureTimestamp = System.currentTimeMillis();

        log.warn("External service '{}' failed ({}/{}): {}",
                serviceName, state.failureCount, DEFAULT_FAILURE_THRESHOLD, reason);

        if (state.failureCount >= DEFAULT_FAILURE_THRESHOLD) {
            state.state = State.OPEN;
            log.error("Circuit breaker for '{}' tripped OPEN. Failing fast for {}ms",
                    serviceName, DEFAULT_RESET_TIMEOUT_MS);
        }
    }

    private <T> T executeFallback(String serviceName, Callable<T> fallback, Throwable cause) {
        if (fallback != null) {
            try {
                log.info("Executing graceful fallback for '{}'", serviceName);
                return fallback.call();
            } catch (Exception fallbackError) {
                log.error("Fallback for '{}' also failed", serviceName, fallbackError);
            }
        }
        if (cause instanceof RuntimeException re) {
            throw re;
        }
        throw new RuntimeException("External service " + serviceName + " failure", cause);
    }

    public State getState(String serviceName) {
        CircuitState state = circuits.get(serviceName);
        return state != null ? state.state : State.CLOSED;
    }

    public synchronized void reset(String serviceName) {
        circuits.remove(serviceName);
    }
}
