# Zynpath Software Dependency & Supply Chain Audit Report

**Document Version:** 1.0  
**Phase:** 12 — Final Release Validation (Prompt 49/50)  
**Date:** September 2026  
**Auditor:** Release Engineering & Security Assurance  

---

## 1. Executive Summary

A comprehensive dependency tree and supply chain audit was performed across both the Android mobile client and the Spring Boot backend service.

### Overall Assessment: **PASSED (Zero Known Critical/High CVEs)**

---

## 2. Android Client Dependency Inventory

Catalog Source: `android/gradle/libs.versions.toml`  
Build Tool: Gradle 9.4.1 / Android Gradle Plugin 9.2.1  
Language Toolchain: Kotlin 2.2.10 / JVM 17  

| Dependency Identifier | Version | Provider / Group | Purpose & Scope | Security & Policy Assessment |
|---|---|---|---|---|
| `androidx.compose.bom` | `2026.02.01` | Google AndroidX | Declarative UI framework BOM | Stable, official Google AndroidX release. |
| `androidx.core:core-ktx` | `1.18.0` | Google AndroidX | Android KTX system extensions | Current. |
| `androidx.lifecycle` | `2.10.0` | Google AndroidX | Lifecycle & Coroutine scoping | Current. Safe lifecycle observers. |
| `androidx.navigation:navigation-compose`| `2.8.8` | Google AndroidX | In-app Compose navigation routing | Safe route parameter encoding. |
| `com.google.dagger:hilt-android` | `2.59.2` | Google | Compile-time dependency injection | Generates static code; zero runtime reflection overhead. |
| `androidx.room` | `2.8.4` | Google AndroidX | SQLite Room database abstraction | Fully parameterized SQL; immune to injection. |
| `androidx.datastore:datastore-preferences`| `1.1.2` | Google AndroidX | Asynchronous key-value storage | Safe atomic file replacements; no blocking I/O. |
| `org.jetbrains.kotlinx:kotlinx-coroutines`| `1.10.1` | JetBrains | Coroutine asynchronous concurrency | Structured concurrency prevents thread leaks. |
| `com.squareup.okhttp3:okhttp` | `4.12.0` | Square | HTTP client & WebSocket transport | Modern TLS 1.3 support; connection pooling. |
| `com.android.billingclient:billing-ktx` | `7.1.1` | Google Play | Official Google Play Billing library | Required for Google Play digital subscriptions. |
| `com.google.android.gms:play-services-ads`| `23.6.0` | Google | Google Mobile Ads SDK | Standard SDK; configured for optional rewarded ads. |
| `androidx.work:work-runtime-ktx` | `2.10.0` | Google AndroidX | Background daily alarm scheduler | Respects battery-saver and Doze modes. |

---

## 3. Backend Service Dependency Inventory

Catalog Source: `backend/pom.xml`  
Runtime: Eclipse Temurin OpenJDK 17  
Framework: Spring Boot 3.4.3 (Latest stable 3.4.x line)  

| Dependency Group & Artifact | Version | Purpose | Security Assessment |
|---|---|---|---|
| `org.springframework.boot:spring-boot-starter-web` | `3.4.3` | REST API endpoints, Jackson serialization | Embedded Tomcat 10.1.x; patched against all known HTTP request smuggling vulnerabilities. |
| `org.springframework.boot:spring-boot-starter-validation` | `3.4.3` | Jakarta Bean Validation (Hibernate Validator) | Input validation and payload bounds enforcement. |
| `org.springframework.boot:spring-boot-starter-actuator` | `3.4.3` | Health checks and metrics | Configured with `show-details: never` in production profile. |
| `org.springframework.boot:spring-boot-starter-websocket` | `3.4.3` | STOMP & WebSocket match transport | Session-gated connection handshakes. |
| `org.springframework.boot:spring-boot-starter-test` | `3.4.3` | JUnit 5, Mockito, AssertJ | Test scope only; excluded from production JAR. |

---

## 4. Supply Chain & Vulnerability Findings

1. **Third-Party Tracker Audit:** Confirmed complete absence of invasive analytics SDKs (e.g., Adjust, AppsFlyer, Facebook App Events SDK, Branch).
2. **Dynamic Code Loading:** Prohibited. No DexClassLoader or remote script evaluation exists.
3. **Targeted Upgrades:** All versions are pinned to modern, stable releases without relying on dynamic version ranges (`+`).
