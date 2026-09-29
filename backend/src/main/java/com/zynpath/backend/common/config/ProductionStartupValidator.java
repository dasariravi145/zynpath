package com.zynpath.backend.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Production startup configuration validator.
 *
 * Implements Prompt 44 Section 10 & 28:
 * - Validates essential production environment variables and security controls upon startup.
 * - Fails fast with clear diagnostic errors when critical production values are missing or insecure.
 * - Protects against accidental execution with default developer placeholders in production.
 */
@Component
public class ProductionStartupValidator implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(ProductionStartupValidator.class);

    private final Environment environment;

    @Value("${zynpath.auth.google.client-id:}")
    private String googleClientId;

    @Value("${zynpath.auth.facebook.app-id:}")
    private String facebookAppId;

    @Value("${zynpath.auth.session.ttl-seconds:2592000}")
    private long sessionTtlSeconds;

    @Value("${zynpath.cors.allowed-origins:}")
    private List<String> corsOrigins;

    @Value("${zynpath.websocket.allowed-origins:*}")
    private String wsOrigins;

    @Value("${server.shutdown:immediate}")
    private String serverShutdown;

    public ProductionStartupValidator(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        boolean isProduction = environment.matchesProfiles("prod", "production");

        if (!isProduction) {
            log.info("Zynpath running in non-production profile [{}]. Skipping strict production constraints.",
                    String.join(",", environment.getActiveProfiles().length > 0 ? environment.getActiveProfiles() : new String[]{"default"}));
            return;
        }

        log.info("Validating production configuration settings for Zynpath modular monolith...");

        List<String> errors = new ArrayList<>();

        // 1. Validate Session Security
        if (sessionTtlSeconds <= 0) {
            errors.add("Session TTL (SESSION_TTL_SECONDS) must be a positive integer.");
        }

        // 2. Validate Google Identity Configuration
        if (googleClientId == null || googleClientId.isBlank() || googleClientId.contains("YOUR_GOOGLE") || googleClientId.startsWith("[")) {
            errors.add("GOOGLE_CLIENT_ID must be set to a valid Google OAuth Client ID in production.");
        }

        // 3. Validate WebSocket Origin Restrictions
        if (wsOrigins != null && wsOrigins.trim().equals("*")) {
            errors.add("WebSocket allowed origins (zynpath.websocket.allowed-origins) cannot be wildcard '*' in production.");
        }

        // 4. Validate CORS Origin Restrictions
        if (corsOrigins != null && (corsOrigins.contains("*") || corsOrigins.contains("http://localhost:*"))) {
            errors.add("CORS allowed origins (zynpath.cors.allowed-origins) cannot contain wildcards or localhost in production.");
        }

        // 5. Fail Fast if Validation Errors Detected
        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("\n=================================================================================\n");
            sb.append("CRITICAL: ZYNPATH PRODUCTION STARTUP VALIDATION FAILED\n");
            sb.append("The backend cannot safely serve production traffic due to invalid configuration:\n");
            for (String err : errors) {
                sb.append("  [!] ").append(err).append("\n");
            }
            sb.append("Please provision the required environment variables before deploying.\n");
            sb.append("=================================================================================\n");

            log.error(sb.toString());
            throw new IllegalStateException("Production startup validation failed: " + errors);
        }

        log.info("Zynpath production startup validation PASSED. All critical configuration constraints satisfied.");
    }
}
