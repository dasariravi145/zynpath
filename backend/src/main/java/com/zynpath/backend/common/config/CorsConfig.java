package com.zynpath.backend.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Environment-aware CORS configuration supporting strict production origins and local development patterns.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${zynpath.cors.allowed-origins:}")
    private List<String> configuredAllowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        CorsRegistration registration = registry.addMapping("/api/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);

        if (configuredAllowedOrigins != null && !configuredAllowedOrigins.isEmpty() && !configuredAllowedOrigins.get(0).isBlank()) {
            registration.allowedOrigins(configuredAllowedOrigins.toArray(new String[0]));
        } else {
            registration.allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*", "http://10.0.2.2:*", "http://192.168.*.*");
        }
    }
}
