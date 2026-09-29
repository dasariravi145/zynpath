package com.zynpath.backend.health;

import com.zynpath.backend.health.model.HealthResponse;
import com.zynpath.backend.security.annotation.RequireAccess;
import com.zynpath.backend.security.model.EndpointAccessTier;
import com.zynpath.backend.security.ratelimit.RateLimitPolicy;
import com.zynpath.backend.security.ratelimit.RateLimited;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health check controller providing liveness confirmation for Android clients and local dev verification.
 */
@RestController
@RequestMapping("/api/v1/health")
@RequireAccess(EndpointAccessTier.PUBLIC)
@RateLimited(RateLimitPolicy.DEFAULT_API)
public class HealthController {

    private final String serviceName;
    private final String apiVersion;
    private final String environment;

    public HealthController(
            @Value("${zynpath.service-name:Zynpath Game Service}") String serviceName,
            @Value("${zynpath.api.version:v1}") String apiVersion,
            @Value("${zynpath.environment:development}") String environment) {
        this.serviceName = serviceName;
        this.apiVersion = apiVersion;
        this.environment = environment;
    }

    @GetMapping
    public ResponseEntity<HealthResponse> getHealth() {
        return ResponseEntity.ok(HealthResponse.healthy(serviceName, apiVersion, environment));
    }
}
