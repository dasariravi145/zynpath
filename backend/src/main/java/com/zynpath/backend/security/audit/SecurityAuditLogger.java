package com.zynpath.backend.security.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Structured, privacy-conscious security audit logger.
 *
 * Implements Prompt 36 Section 58, 59, 60, 73:
 * - Emits audit logs with timestamps, event types, actor IDs, client IPs, and safe diagnostic contexts.
 * - Actively scrubs and redacts sensitive credentials, tokens, and PII.
 */
@Component
public class SecurityAuditLogger {

    private static final Logger log = LoggerFactory.getLogger("com.zynpath.security.audit");

    private static final Pattern TOKEN_PATTERN = Pattern.compile("(?i)(zyn_[a-zA-Z0-9_-]{10,}|bearer\\s+[a-zA-Z0-9._-]+)");
    private static final Pattern PURCHASE_TOKEN_PATTERN = Pattern.compile("(?i)(purchaseToken[\"']?\\s*:\\s*[\"'])([^\"']+)([\"'])");

    /**
     * Convenience method for logging audit events from controllers.
     */
    public void logEvent(SecurityEventType eventType, String actorPlayerId, Map<String, ?> details) {
        recordEvent(
                eventType,
                actorPlayerId,
                com.zynpath.backend.security.context.SecurityContext.getClientIp(),
                "API",
                "SUCCESS",
                details != null ? new java.util.HashMap<>(details) : java.util.Collections.emptyMap()
        );
    }

    /**
     * Records a security event with structured context.
     */
    public void recordEvent(
            SecurityEventType eventType,
            String actorPlayerId,
            String clientIp,
            String targetResource,
            String outcome,
            Map<String, Object> details
    ) {
        String safeActor = (actorPlayerId != null && !actorPlayerId.isBlank()) ? actorPlayerId : "ANONYMOUS";
        String safeIp = (clientIp != null && !clientIp.isBlank()) ? clientIp : "UNKNOWN_IP";
        String safeResource = (targetResource != null) ? sanitize(targetResource) : "N/A";
        String safeOutcome = (outcome != null) ? outcome : "INFO";

        StringBuilder sb = new StringBuilder();
        sb.append("[SECURITY_AUDIT] event=").append(eventType.name())
          .append(" actor=").append(safeActor)
          .append(" ip=").append(safeIp)
          .append(" resource=").append(safeResource)
          .append(" outcome=").append(safeOutcome);

        if (details != null && !details.isEmpty()) {
            sb.append(" details={");
            boolean first = true;
            for (Map.Entry<String, Object> entry : details.entrySet()) {
                if (!first) sb.append(", ");
                String key = entry.getKey();
                String valueStr = String.valueOf(entry.getValue());
                sb.append(key).append("=").append(sanitize(valueStr));
                first = false;
            }
            sb.append("}");
        }

        switch (eventType) {
            case AUTH_FAILURE, AUTHORIZATION_DENIED, RATE_LIMIT_EXCEEDED,
                 INVALID_COMPLETION_CLAIM, SUSPICIOUS_REWARD_ATTEMPT,
                 INVITATION_SPAM_BLOCKED, WEBSOCKET_UNAUTHORIZED, PUZZLE_INTEGRITY_VIOLATION ->
                log.warn(sb.toString());
            default ->
                log.info(sb.toString());
        }
    }

    /**
     * Scrubs sensitive tokens, credentials, or potential injection strings.
     */
    public static String sanitize(String input) {
        if (input == null) return "null";
        String sanitized = TOKEN_PATTERN.matcher(input).replaceAll("[REDACTED_TOKEN]");
        sanitized = PURCHASE_TOKEN_PATTERN.matcher(sanitized).replaceAll("$1[REDACTED_PURCHASE_TOKEN]$3");
        // Strip control characters to prevent log injection
        return sanitized.replaceAll("[\r\n\t]", " ");
    }
}
