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

    @Value("${zynpath.persistence.mode:in-memory}")
    private String persistenceMode;

    @Value("${spring.datasource.url:}")
    private String datasourceUrl;

    @Value("${spring.datasource.username:}")
    private String datasourceUsername;

    @Value("${spring.datasource.password:}")
    private String datasourcePassword;

    @Value("${zynpath.api.base-url:}")
    private String backendBaseUrl;

    @Value("${zynpath.ads.ssv.enforce-signature:true}")
    private boolean enforceSsvSignature = true;

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
        if (wsOrigins == null || wsOrigins.isBlank()) {
            errors.add("WebSocket allowed origins (zynpath.websocket.allowed-origins / WS_ALLOWED_ORIGINS) must be explicitly configured in production.");
        } else if (wsOrigins.trim().equals("*")) {
            errors.add("WebSocket allowed origins (zynpath.websocket.allowed-origins) cannot be wildcard '*' in production.");
        }

        // 4. Validate CORS Origin Restrictions
        if (corsOrigins == null || corsOrigins.isEmpty() || corsOrigins.stream().allMatch(String::isBlank)) {
            errors.add("CORS allowed origins (zynpath.cors.allowed-origins / CORS_ALLOWED_ORIGINS) must be explicitly configured in production.");
        } else if (corsOrigins.contains("*") || corsOrigins.contains("http://localhost:*")) {
            errors.add("CORS allowed origins (zynpath.cors.allowed-origins) cannot contain wildcards or localhost in production.");
        }

        // 5. Validate PostgreSQL Persistence in Production
        if ("in-memory".equalsIgnoreCase(persistenceMode)) {
            errors.add("Production CANNOT run with volatile in-memory storage (zynpath.persistence.mode=in-memory). "
                    + "Set zynpath.persistence.mode=jpa to enable durable PostgreSQL persistence.");
        }
        if (datasourceUrl == null || datasourceUrl.isBlank() || datasourceUrl.contains("localhost")) {
            errors.add("SPRING_DATASOURCE_URL must be set to a valid remote PostgreSQL database in production.");
        }
        if (datasourceUsername == null || datasourceUsername.isBlank()) {
            errors.add("SPRING_DATASOURCE_USERNAME must be set for PostgreSQL in production.");
        }
        if (datasourcePassword == null || datasourcePassword.isBlank()) {
            errors.add("SPRING_DATASOURCE_PASSWORD must be set for PostgreSQL in production.");
        }

        // 6. Validate Production Backend Endpoint Configuration
        if (backendBaseUrl == null || backendBaseUrl.isBlank()) {
            errors.add("BACKEND_BASE_URL (zynpath.api.base-url) must be configured with a verified HTTPS endpoint in production (e.g. Cloud Run https://<service>-<hash>-<region>.a.run.app or verified custom domain).");
        } else {
            String lowerUrl = backendBaseUrl.trim().toLowerCase();
            if (!lowerUrl.startsWith("https://")) {
                errors.add("BACKEND_BASE_URL must use HTTPS in production. Cleartext HTTP is prohibited: " + backendBaseUrl);
            }
            if (lowerUrl.contains("localhost") || lowerUrl.contains("127.0.0.1") || lowerUrl.contains("10.0.2.2") || lowerUrl.contains("192.168.")) {
                errors.add("BACKEND_BASE_URL cannot use localhost, emulator, or private LAN addresses in production: " + backendBaseUrl);
            }
            if (lowerUrl.contains("api.zynpath.com") || lowerUrl.contains("api.zynpath.app")) {
                errors.add("BACKEND_BASE_URL uses an unverified placeholder domain (" + backendBaseUrl + "). Production requires a deployed and verified HTTPS endpoint.");
            }
        }

        // 7. Validate AdMob SSV Signature Verification Enforcement in Production
        if (!enforceSsvSignature) {
            errors.add("AdMob SSV signature verification (zynpath.ads.ssv.enforce-signature) cannot be disabled in production.");
        }

        // 8. Fail Fast if Validation Errors Detected
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
