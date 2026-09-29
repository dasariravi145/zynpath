package com.zynpath.backend.security.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zynpath.backend.common.model.ErrorResponse;
import com.zynpath.backend.security.audit.SecurityAuditLogger;
import com.zynpath.backend.security.audit.SecurityEventType;
import com.zynpath.backend.security.context.SecurityContext;
import com.zynpath.backend.security.ratelimit.RateLimitPolicy;
import com.zynpath.backend.security.ratelimit.RateLimited;
import com.zynpath.backend.security.ratelimit.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Map;

/**
 * REST interceptor enforcing tiered sliding-window rate limits.
 *
 * Implements Prompt 36 Section 50, 51, 52:
 * - Emits HTTP 429 Too Many Requests with standard Retry-After header.
 * - Keyed by authenticated playerId or client IP for unauthenticated routes.
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimiterService rateLimiterService;
    private final SecurityAuditLogger auditLogger;
    private final ObjectMapper objectMapper;

    public RateLimitInterceptor(
            RateLimiterService rateLimiterService,
            SecurityAuditLogger auditLogger,
            ObjectMapper objectMapper
    ) {
        this.rateLimiterService = rateLimiterService;
        this.auditLogger = auditLogger;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        RateLimitPolicy policy = resolvePolicy(request.getRequestURI(), handler);

        // Identify requester: authenticated playerId takes precedence over raw IP
        String identity = SecurityContext.getOptionalPlayerId();
        if (identity == null || identity.isBlank()) {
            identity = SecurityContext.getClientIp();
        }

        if (!rateLimiterService.tryAcquire(identity, policy)) {
            long retryAfter = rateLimiterService.getRetryAfterSeconds(identity, policy);
            auditLogger.recordEvent(
                    SecurityEventType.RATE_LIMIT_EXCEEDED,
                    identity,
                    SecurityContext.getClientIp(),
                    request.getRequestURI(),
                    "THROTTLED",
                    Map.of("policy", policy.name(), "retryAfterSeconds", retryAfter)
            );

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("Retry-After", String.valueOf(retryAfter));
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            ErrorResponse error = ErrorResponse.of(
                    HttpStatus.TOO_MANY_REQUESTS.value(),
                    "Too Many Requests",
                    "Rate limit exceeded for " + policy.name() + ". Please retry in " + retryAfter + " seconds.",
                    request.getRequestURI(),
                    "rl_" + System.currentTimeMillis()
            );
            response.getWriter().write(objectMapper.writeValueAsString(error));
            return false;
        }

        return true;
    }

    private RateLimitPolicy resolvePolicy(String uri, Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            RateLimited methodAnn = handlerMethod.getMethodAnnotation(RateLimited.class);
            if (methodAnn != null) return methodAnn.value();

            RateLimited classAnn = handlerMethod.getBeanType().getAnnotation(RateLimited.class);
            if (classAnn != null) return classAnn.value();
        }

        if (uri.startsWith("/api/v1/auth/")) return RateLimitPolicy.AUTH;
        if (uri.contains("/delete") || uri.contains("/export")) return RateLimitPolicy.SENSITIVE;
        if (uri.contains("/invitations") || uri.contains("/leagues") || uri.contains("/presence")) return RateLimitPolicy.INVITATIONS_AND_ROOMS;
        if (uri.contains("/matchmaking")) return RateLimitPolicy.MATCHMAKING;
        if (uri.contains("/claim") || uri.contains("/sync") || uri.contains("/verify")) return RateLimitPolicy.SUBMISSIONS;

        return RateLimitPolicy.DEFAULT_API;
    }
}
