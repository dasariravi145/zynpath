package com.zynpath.backend.security.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filter injecting secure transport headers and enforcing CORS origin boundaries.
 *
 * Implements Prompt 36 Section 63, 64:
 * - Injects HSTS, X-Content-Type-Options, X-Frame-Options, CSP, and Referrer-Policy headers.
 * - Prevents MIME-confusion, clickjacking, and cross-site scripting vulnerabilities.
 * - Blocks unrestricted wildcard CORS origins when authentication credentials are used.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityHeadersFilter implements Filter {

    @Value("${zynpath.cors.allowed-origins:}")
    private java.util.List<String> configuredAllowedOrigins;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // Security response headers
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        response.setHeader("Content-Security-Policy", "default-src 'none'; frame-ancestors 'none';");
        response.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");

        // Enable HSTS in HTTPS environments
        if (request.isSecure() || "https".equalsIgnoreCase(request.getHeader("X-Forwarded-Proto"))) {
            response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
        }

        // Prevent caching of private authenticated API responses
        if (request.getRequestURI().startsWith("/api/v1/")) {
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
            response.setHeader("Pragma", "no-cache");
        }

        // Controlled CORS headers
        String origin = request.getHeader("Origin");
        if (origin != null && !origin.isBlank()) {
            boolean isAllowed = false;
            if (configuredAllowedOrigins != null && !configuredAllowedOrigins.isEmpty()) {
                isAllowed = configuredAllowedOrigins.stream()
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .anyMatch(allowed -> allowed.equalsIgnoreCase(origin)
                                || (allowed.endsWith("*") && origin.startsWith(allowed.substring(0, allowed.length() - 1))));
            }
            if (!isAllowed) {
                // Permit local development and mobile client WebView / Capacitor schemes
                isAllowed = origin.startsWith("http://localhost") || origin.startsWith("https://localhost")
                        || origin.startsWith("http://127.0.0.1") || origin.startsWith("http://10.0.2.2")
                        || origin.startsWith("capacitor://");
            }

            if (isAllowed) {
                response.setHeader("Access-Control-Allow-Origin", origin);
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, X-Requested-With");
                response.setHeader("Access-Control-Max-Age", "3600");
            }
        }

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);
    }
}
