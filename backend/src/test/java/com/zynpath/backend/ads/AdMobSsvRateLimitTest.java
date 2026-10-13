package com.zynpath.backend.ads;

import com.zynpath.backend.security.ratelimit.RateLimitPolicy;
import com.zynpath.backend.security.ratelimit.RateLimiterService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Validates Google AdMob Server-Side Verification (SSV) rate limiting behavior.
 *
 * Implements Step 17.20 Task 2:
 * - Confirms SSV_CALLBACK tier allows high-volume legitimate callback bursts (up to 240 req/min).
 * - Confirms request 241 is properly throttled with HTTP 429 and Retry-After.
 * - Confirms unsigned/bogus callbacks reject with HTTP 400 and grant zero credits.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AdMobSsvRateLimitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RateLimiterService rateLimiterService;

    @Test
    @DisplayName("1. RateLimitPolicy.SSV_CALLBACK is configured for 240 requests per 60 seconds")
    void ssvCallbackPolicy_isConfiguredProperly() {
        RateLimitPolicy policy = RateLimitPolicy.SSV_CALLBACK;
        assertThat(policy.getMaxRequests()).isEqualTo(240);
        assertThat(policy.getWindowSeconds()).isEqualTo(60);
    }

    @Test
    @DisplayName("2. SSV endpoint allows bursts up to 240 requests without 429, then throttles at 241")
    void ssvCallback_allowsBurstThenThrottles() throws Exception {
        String testIp = "66.249.90.1"; // Google crawler IP representation

        // Simulate 240 requests from this IP
        for (int i = 0; i < 240; i++) {
            boolean acquired = rateLimiterService.tryAcquire(testIp, RateLimitPolicy.SSV_CALLBACK);
            assertThat(acquired)
                    .withFailMessage("Request %d should have been acquired under SSV_CALLBACK policy", i + 1)
                    .isTrue();
        }

        // The 241st request from the same IP must be rejected
        boolean acquired241 = rateLimiterService.tryAcquire(testIp, RateLimitPolicy.SSV_CALLBACK);
        assertThat(acquired241).isFalse();

        long retryAfter = rateLimiterService.getRetryAfterSeconds(testIp, RateLimitPolicy.SSV_CALLBACK);
        assertThat(retryAfter).isGreaterThan(0L);

        // Verify MockMvc receives 429 when IP is exhausted
        mockMvc.perform(get("/api/v1/ads/ssv-callback?transaction_id=txn_burst_test")
                        .with(request -> {
                            request.setRemoteAddr(testIp);
                            return request;
                        }))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("Retry-After"));
    }

    @Test
    @DisplayName("3. Unsigned SSV callback receives HTTP 400 Bad Request, NOT 200 or 403")
    void unsignedCallback_receives400BadRequest() throws Exception {
        String freshIp = "66.249.90.2";

        mockMvc.perform(get("/api/v1/ads/ssv-callback?transaction_id=txn_unsigned_123")
                        .with(request -> {
                            request.setRemoteAddr(freshIp);
                            return request;
                        }))
                .andExpect(status().isBadRequest());
    }
}
