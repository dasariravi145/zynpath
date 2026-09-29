package com.zynpath.backend.security.ratelimit;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory sliding window rate limiter service.
 *
 * Implements Prompt 36 Section 50, 51, 52:
 * - Thread-safe tracking by (identity, policy).
 * - Identity combines authenticated playerId (if present) or remote IP address.
 * - Prevents unbounded memory growth via window resets and stale entry cleanup.
 */
@Service
public class RateLimiterService {

    private static class WindowCounter {
        private final long windowStartEpochMs;
        private final AtomicInteger count;

        public WindowCounter(long windowStartEpochMs) {
            this.windowStartEpochMs = windowStartEpochMs;
            this.count = new AtomicInteger(1);
        }
    }

    private final Map<String, WindowCounter> windowMap = new ConcurrentHashMap<>();

    /**
     * Attempts to acquire an execution ticket for the given identity and policy.
     *
     * @return true if request is within limits, false if rate limited.
     */
    public boolean tryAcquire(String identity, RateLimitPolicy policy) {
        if (identity == null || identity.isBlank()) {
            identity = "unknown_client";
        }
        String key = policy.name() + ":" + identity;
        long now = System.currentTimeMillis();
        long windowDurationMs = policy.getWindowSeconds() * 1000L;

        WindowCounter counter = windowMap.compute(key, (k, existing) -> {
            if (existing == null || (now - existing.windowStartEpochMs) >= windowDurationMs) {
                return new WindowCounter(now);
            }
            existing.count.incrementAndGet();
            return existing;
        });

        return counter.count.get() <= policy.getMaxRequests();
    }

    /**
     * Calculates the remaining seconds the client must wait before retrying.
     */
    public long getRetryAfterSeconds(String identity, RateLimitPolicy policy) {
        String key = policy.name() + ":" + identity;
        WindowCounter counter = windowMap.get(key);
        if (counter == null) return 1L;

        long now = System.currentTimeMillis();
        long elapsed = now - counter.windowStartEpochMs;
        long remainingMs = (policy.getWindowSeconds() * 1000L) - elapsed;
        return Math.max(1L, (remainingMs + 999L) / 1000L);
    }

    /**
     * Maintenance cleanup to evict expired counters.
     */
    public void cleanupExpiredWindows() {
        long now = System.currentTimeMillis();
        windowMap.entrySet().removeIf(entry -> {
            long windowMs = 300_000L; // Evict entries older than 5 minutes
            return (now - entry.getValue().windowStartEpochMs) > windowMs;
        });
    }
}
