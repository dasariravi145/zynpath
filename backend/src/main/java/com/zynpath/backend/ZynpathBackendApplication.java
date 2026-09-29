package com.zynpath.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Zynpath Backend Application - Spring Boot 3 Modular Monolith
 * Provides health checks, future WebSocket match orchestration, and authoritative validation.
 */
@SpringBootApplication
public class ZynpathBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZynpathBackendApplication.class, args);
    }
}
