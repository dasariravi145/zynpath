# Zynpath Backend Architecture

## 1. System Overview
The Zynpath backend is architected as a lean, cohesive **Modular Monolith** using **Java 17+** and **Spring Boot 3.4.3**. It avoids premature microservices complexity and unnecessary operational overhead while enforcing clean architectural boundaries across distinct bounded contexts.

Key Tenet: **Offline-First Guarantee**. The backend serves exclusively competitive multiplayer, cloud validation, synchronized player profiles, and social features. Solo Play, puzzle drawing, local undo/reset, and campaign progression remain 100% offline-capable on the Android client.

---

## 2. Technology Stack
- **Language**: Java 17 LTS (Amazon Corretto 17.0.12 verified)
- **Framework**: Spring Boot 3.4.3
- **Dependency Management**: Apache Maven 3.9+ (`pom.xml`)
- **Web Layer**: Spring MVC (`spring-boot-starter-web`)
- **Validation**: Jakarta Bean Validation (`spring-boot-starter-validation`)
- **Observability**: Spring Boot Actuator (`spring-boot-starter-actuator`)
- **Testing**: Spring Boot Test (`spring-boot-starter-test`, JUnit 5, Mockito)

---

## 3. Package Structure & Modular Monolith Organization

```
com.zynpath.backend
├── ZynpathBackendApplication.java    # Spring Boot Main Entry Point
├── common                           # Cross-cutting API responses, errors, CORS
│   ├── ApiResponse.java             # Standard envelope
│   ├── ErrorResponse.java           # RFC 7807 compatible error model
│   ├── GlobalExceptionHandler.java  # Sanitized exception translation (no leaked stack traces)
│   └── CorsConfig.java              # Development CORS filter
├── config                           # Spring configurations & beans
├── health                           # Liveness and diagnostic probes
│   ├── HealthResponse.java
│   └── HealthController.java        # GET /api/v1/health
├── auth                             # Future authentication boundary (Google/Facebook/Guest)
│   ├── AuthClaims.java
│   └── AuthenticationBoundaryService.java
├── player                           # Player profile and account data
│   ├── PlayerProfileSummary.java
│   └── PlayerBoundaryService.java
├── puzzle                           # Server-side authoritative puzzle verification
│   ├── CompetitiveValidationClaim.java
│   ├── ValidationOutcome.java
│   └── AuthoritativePuzzleValidator.java
├── duel                             # 1v1 Quick Duel & Friend Duel session state
│   └── DuelSessionState.java
├── matchmaking                      # FIFO / MMR matchmaking queue interface
│   └── MatchmakingQueueService.java
├── league                           # 2–5 player Mini League groups
│   └── MiniLeagueSummary.java
├── leaderboard                      # Global and regional competitive rankings
│   └── LeaderboardEntry.java
├── subscription                     # AdMob & Google Play Billing entitlement verification
│   ├── SubscriptionTier.java
│   └── EntitlementService.java
└── reaction                         # In-memory ephemeral reaction relay
    ├── ReactionType.java            # Curated 7 reactions (no arbitrary chat)
    ├── EphemeralReactionEvent.java
    └── ReactionRelayService.java
```

---

## 4. Module Boundaries & Responsibilities

### A. Health & Diagnostics (`health`)
- Exposes unauthenticated `GET /api/v1/health` for dev probing, container liveness checks, and Android connectivity diagnostics.
- Returns status, service name, version, timestamp, and active environment.
- Never leaks internal hostnames, credentials, or environment variables.

### B. Competitive Puzzle Validation (`puzzle`)
- **Authoritative Rules**: The server does NOT trust client-supplied completion booleans.
- **Rule Verification Pipeline**:
  1. Ascending checkpoint visits ($1 \rightarrow 2 \rightarrow \dots \rightarrow N$).
  2. Orthogonal movement only (Manhattan distance = 1).
  3. No edge crossing through walls or grid boundaries.
  4. Single-visit cell coverage: zero self-intersections.
  5. Complete coverage: Every required grid cell must be occupied ($L = W \times H - \text{blocked}$).
  6. Final checkpoint termination: The final step must land on checkpoint $N$.

### C. Ephemeral Reactions (`reaction`)
- Predefined quick-react phrases and emojis only:
  - `WOW`, `NICE`, `GG`, `WELL_PLAYED`, `GOOD_LUCK`, `AMAZING`, `REMATCH`
- Rate-limited and validated against active match session participants.
- In-memory dispatch; zero persistence to disk or database. Discarded immediately when match ends.

### D. Billing & Entitlements (`subscription`)
- Server-side verification for Google Play Billing purchase tokens.
- Tiers: `FREE`, `MONTHLY` (₹99/month), `HALF_YEARLY` (₹499/6 months).
- Pure cosmetic/ad-free entitlement; strictly NO competitive advantages or hints in duel mode.

---

## 5. Security & Error Handling
1. **Sanitized Errors**: `GlobalExceptionHandler` converts all exceptions into structured `ErrorResponse` objects with unique request tracking IDs. Client responses never expose JVM stack traces or database error messages.
2. **CORS Policy**: Configured to restrict origins in production, allowing local development tooling on localhost and emulator hosts.
3. **Actuator Isolation**: Production deployment isolates actuator endpoints behind administrative network boundaries.
