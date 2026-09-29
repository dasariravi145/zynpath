package com.zynpath.backend.security.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zynpath.backend.auth.model.AuthErrorResponse;
import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.auth.model.PlayerSession;
import com.zynpath.backend.auth.service.SessionSecurityService;
import com.zynpath.backend.security.annotation.RequireAccess;
import com.zynpath.backend.security.audit.SecurityAuditLogger;
import com.zynpath.backend.security.audit.SecurityEventType;
import com.zynpath.backend.security.context.SecurityContext;
import com.zynpath.backend.security.model.EndpointAccessTier;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Centralized security interceptor enforcing authentication and deny-by-default access policies.
 *
 * Implements Prompt 36 Section 5, 7, 8, 9, 15, 16:
 * - Deny-by-default: protected endpoints require a valid, non-expired PlayerSession bearer token.
 * - Extracts client IP and user-agent into SecurityContext.
 * - Protects authenticated resources while maintaining public access to login, catalog, and health.
 */
@Component
public class SecurityInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(SecurityInterceptor.class);

    private static final Set<String> PUBLIC_PATH_PREFIXES = Set.of(
            "/api/v1/auth/exchange",
            "/api/v1/auth/google",
            "/api/v1/auth/facebook",
            "/api/v1/auth/link",
            "/api/v1/auth/refresh",
            "/api/v1/health",
            "/actuator",
            "/api/v1/cosmetics/catalog",
            "/api/v1/cosmetics/public",
            "/api/v1/daily/challenge",
            "/api/v1/daily/leaderboard",
            "/api/v1/multiplayer/leaderboard",
            "/api/v1/ads/ssv-callback",
            "/api/v1/content/packs"
    );

    private final SessionSecurityService sessionSecurityService;
    private final SecurityAuditLogger auditLogger;
    private final ObjectMapper objectMapper;

    public SecurityInterceptor(
            SessionSecurityService sessionSecurityService,
            SecurityAuditLogger auditLogger,
            ObjectMapper objectMapper
    ) {
        this.sessionSecurityService = sessionSecurityService;
        this.auditLogger = auditLogger;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = extractClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        String uri = request.getRequestURI();

        // 1. Check if resource is public via path whitelist or @RequireAccess annotation
        boolean isPublic = isPublicEndpoint(uri, handler);

        String authHeader = request.getHeader("Authorization");
        PlayerSession session = null;

        if (authHeader != null && !authHeader.isBlank()) {
            try {
                session = sessionSecurityService.validateSession(authHeader);
            } catch (AuthException ex) {
                if (!isPublic) {
                    auditLogger.recordEvent(
                            SecurityEventType.AUTH_FAILURE,
                            null,
                            clientIp,
                            uri,
                            "DENIED",
                            Map.of("reason", ex.getMessage(), "code", ex.getErrorCode())
                    );
                    sendUnauthorizedResponse(response, ex.getErrorCode(), ex.getMessage());
                    return false;
                }
            }
        }

        if (!isPublic && session == null) {
            auditLogger.recordEvent(
                    SecurityEventType.AUTH_FAILURE,
                    null,
                    clientIp,
                    uri,
                    "DENIED",
                    Map.of("reason", "MISSING_BEARER_TOKEN")
            );
            sendUnauthorizedResponse(response, "UNAUTHENTICATED", "Valid authorization bearer token required");
            return false;
        }

        // Establish thread-local SecurityContext
        SecurityContext.set(new SecurityContext(session, clientIp, userAgent));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        SecurityContext.clear();
    }

    private boolean isPublicEndpoint(String uri, Object handler) {
        for (String publicPrefix : PUBLIC_PATH_PREFIXES) {
            if (uri.startsWith(publicPrefix)) {
                return true;
            }
        }

        if (handler instanceof HandlerMethod handlerMethod) {
            RequireAccess methodAnnotation = handlerMethod.getMethodAnnotation(RequireAccess.class);
            if (methodAnnotation != null) {
                return methodAnnotation.value() == EndpointAccessTier.PUBLIC;
            }
            RequireAccess classAnnotation = handlerMethod.getBeanType().getAnnotation(RequireAccess.class);
            if (classAnnotation != null) {
                return classAnnotation.value() == EndpointAccessTier.PUBLIC;
            }
        }

        return false;
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String errorCode, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        AuthErrorResponse error = new AuthErrorResponse(errorCode, message, System.currentTimeMillis());
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }

    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
