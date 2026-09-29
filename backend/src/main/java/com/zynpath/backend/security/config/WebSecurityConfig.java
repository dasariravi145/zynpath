package com.zynpath.backend.security.config;

import com.zynpath.backend.security.interceptor.RateLimitInterceptor;
import com.zynpath.backend.security.interceptor.SecurityInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Web MVC configuration registering security and rate limiting interceptors.
 *
 * Implements Prompt 36 Section 6, 7, 8, 50:
 * - Chains SecurityInterceptor (authentication / authorization) first.
 * - Chains RateLimitInterceptor second.
 */
@Configuration
public class WebSecurityConfig implements WebMvcConfigurer {

    private final SecurityInterceptor securityInterceptor;
    private final RateLimitInterceptor rateLimitInterceptor;

    public WebSecurityConfig(
            SecurityInterceptor securityInterceptor,
            RateLimitInterceptor rateLimitInterceptor
    ) {
        this.securityInterceptor = securityInterceptor;
        this.rateLimitInterceptor = rateLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Enforce centralized session validation & deny-by-default access
        registry.addInterceptor(securityInterceptor)
                .addPathPatterns("/api/**")
                .order(1);

        // Enforce tiered sliding-window rate limiting
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/**")
                .order(2);
    }
}
