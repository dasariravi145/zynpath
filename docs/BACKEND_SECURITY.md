# Zynpath Backend Security Architecture

## 1. Overview and Core Philosophy
Zynpath enforces a strict **Zero-Trust Client Boundary** policy across all online APIs, real-time multiplayer sockets, billing systems, and competitive rankings. The client application (Android Kotlin / Jetpack Compose) performs rich local UX validation and optimistic UI transitions, but is never trusted as an authoritative decision boundary for server state.

Key security tenets:
1. **Deny-by-Default API Gateway**: All incoming endpoints are denied unless explicitly annotated as `@RequireAccess(EndpointAccessTier.PUBLIC)` or validated against an active authenticated session.
2. **Object-Level & Resource Ownership Authorization**: Verification of ownership, match membership, or room participation occurs at the domain service layer using `ResourceAuthorizationService`.
3. **Decoupled Identity & Ephemeral Session Tokens**: Third-party provider tokens (Google OAuth2 ID tokens, Facebook User Access tokens) are verified server-side only during exchange/linking. Internal session tokens (`zyn_<random>`) are decoupled, cryptographically random, and server-revocable.
4. **Authoritative Competitive Gameplay Integrity**: Solves, checkpoint sequences, walls (blocked edges), grid coverage, and match timings are verified by `ServerPuzzleValidator` and `CompetitiveIntegrityGuard`.
5. **Defense-in-Depth Abuse Prevention**: Sliding-window rate limiting (`RateLimiterService`), transaction deduplication (`BillingIntegrityGuard`), social request caps (`SocialAbuseGuard`), and structured audit logging (`SecurityAuditLogger`).
6. **Guest-First Offline Solo Continuity**: Security controls strictly govern online multiplayer, daily leaderboards, cloud sync, and billing without imposing authentication walls on local Solo puzzles.

---

## 2. Security Component Architecture

```mermaid
graph TD
    Client[Android Client / Web Socket] --> Filter[SecurityHeadersFilter]
    Filter --> SecInt[SecurityInterceptor (Deny-by-Default)]
    SecInt --> RateInt[RateLimitInterceptor (Sliding Window)]
    RateInt --> Controller[Spring Boot REST / WS Handler]
    Controller --> Guards[Security Guards]
    Guards --> ResAuth[ResourceAuthorizationService]
    Guards --> CompGuard[CompetitiveIntegrityGuard]
    Guards --> BillGuard[BillingIntegrityGuard]
    Guards --> SocGuard[SocialAbuseGuard]
    Guards --> Audit[SecurityAuditLogger]
```

### Components
- `SecurityHeadersFilter`: Applies standard security response headers (`Strict-Transport-Security`, `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY`, `Content-Security-Policy`, and restricted CORS policies).
- `SecurityInterceptor`: Validates `Authorization: Bearer <token>` against `SessionSecurityService`, initializes the thread-local `SecurityContext`, and enforces deny-by-default access tiers.
- `RateLimitInterceptor`: Inspects endpoint policy (`RateLimitPolicy`) and client identity (authenticated `playerId` or remote IP) to enforce sliding window rate limiting.
- `SecurityContext`: Thread-local storage of validated `PlayerSession`, remote IP address, and User-Agent header, cleared upon request completion.
- `SecurityAuditLogger`: Structured JSON security event logger that automatically sanitizes sensitive fields (tokens, passwords, PII, auth headers).
- `GlobalExceptionHandler`: Sanitizes internal exceptions into structured, non-leaking JSON error payloads with tracking correlation IDs.

---

## 3. Threat Model and Mitigations

| Threat Category | Potential Attack Vector | Applied Mitigation |
| :--- | :--- | :--- |
| **Authentication Bypass** | Forged or unverified Google/Facebook tokens | Backend RSA/JWK signature verification via Google/Facebook verification services; no unverified claims trusted. |
| **Cross-Account Impersonation** | Client passes arbitrary `playerId` in query or body | All controllers resolve acting identity exclusively from validated session principal via `SecurityContext` / `SessionSecurityService`. |
| **Competitive Result Falsification** | Modded APK submits fake victory payload or false time | Server-side `ServerPuzzleValidator` verifies complete coordinate path, checkpoints, walls, and cell coverage. Authoritative server time bounds duration. |
| **Leaderboard Infiltration** | Direct POST to submit high score | Only server-verified competitive matches and daily challenge runs are published to leaderboards; client scores rejected. |
| **Social / Invitation Spam** | Automated script sends thousands of friend or duel invites | `SocialAbuseGuard` limits pending outgoing friend requests to 50, blocks self-requests, and checks block lists; rate-limited endpoints. |
| **Billing Token Replay** | Replaying a valid Google Play purchase token on multiple accounts | `BillingIntegrityGuard` records and validates purchase token ownership uniqueness across player accounts. |
| **Rewarded Ad Duplication** | Replaying AdMob SSV callbacks or client claim requests | Transactional idempotency via `BillingIntegrityGuard.assertRewardTransactionUnique` prevents duplicate hint grants. |
| **Denial of Service** | Flooding matchmaking or WebSocket endpoints | Per-IP and per-player sliding window rate limiting (`RateLimitPolicy.MATCHMAKING`, `INVITATIONS_AND_ROOMS`, etc.) plus per-socket WebSocket message throttling (20 msg/sec). |
| **Information Disclosure** | Stack traces or database errors leaking schema info | `GlobalExceptionHandler` converts all exceptions to safe, opaque error objects with correlation request IDs. |

---

## 4. Verification & Testing Policy
- **Essential Compilation**: Verified via targeted Maven test-compilation.
- **Full Security Test Suite**: PASSED (Prompt 47).
- **Status**: IMPLEMENTED / All Security Regression Suites PASSED (100%).

---

## 5. Google Play Release Security & Policy Compliance (Prompt 43)
- **Production Cleartext Traffic Elimination**: `network-security-config.xml` strictly enforces `cleartextTrafficPermitted="false"` across all production builds. Development overrides are strictly constrained to `10.0.2.2` and `localhost`.
- **Authoritative Account Deletion API**: Endpoints `@DeleteMapping("/api/v1/account/delete")` and `@PostMapping("/api/v1/account/delete")` enforce `RateLimitPolicy.SENSITIVE` and derive identity strictly from validated thread-local `SecurityContext`, permanently purging player records while preserving anonymized competitive match integrity.
- **Exclusion from Device Backup**: Keystore-encrypted session secrets (`zyn_secure_session.enc`) are excluded from Android Auto Backup via `data_extraction_rules.xml` to prevent cross-device decryption crashes.

---

## 6. Production Infrastructure & Secret Isolation Security (Prompt 44)
- **Fail-Fast Startup Validation**: `ProductionStartupValidator.java` halts JVM initialization if Google Client ID is unconfigured/placeholder, if session TTL is non-positive, or if wildcard (`*`) origins are configured in production.
- **Strict DDL Validation**: `spring.jpa.hibernate.ddl-auto: validate` prevents accidental schema drop-and-create operations. Schema changes are strictly managed via ordered Flyway migrations (`V1`–`V6`).
- **Internal Topology Concealment**: Actuator probes `/actuator/health/liveness` and `/readiness` enforce `show-details: never` in production, concealing database hosts and pool states from unauthenticated probes.
- **Structured Log Sanitization**: Bearer tokens and billing identifiers are scrubbed via precompiled regex in `SecurityAuditLogger.java`, and SQL statement parameter binding logging is disabled.
- **Container Isolation**: Multi-stage `Dockerfile` executes Spring Boot under an unprivileged non-root user (`zynpath:10001`), preventing container breakout or unauthorized host system access.

---

## 7. Backend Security Regression Verification (Prompt 47)

### 7.1 Test Execution Summary (`SecurityRegressionTest`)
- **Suite**: `com.zynpath.backend.security.SecurityRegressionTest`
- **Total Tests**: 7 / 7 PASSED (100% Pass Rate).
- **Verified Mitigations**:
  1. `testBOLA_crossAccountDataAccess_shouldBeRejected`: Verified Broken Object-Level Authorization (BOLA/IDOR) prevention; accessing another player's profile data returns 403 Forbidden.
  2. `testMatchAuthorization_nonParticipant_shouldBeRejected`: Verified unauthorized match access prevention; non-participants cannot query or submit moves to foreign matches.
  3. `testInvitationAbuse_nonRecipient_shouldBeRejected`: Non-recipients cannot accept or decline invites.
  4. `testDuplicateRewardClaims_shouldBeRejected`: Verified replay attack prevention; replayed reward claims are rejected with 400 Bad Request.
  5. `testInvalidTokens_shouldBeRejected`: Malformed, tampered, or expired tokens are rejected with 401 Unauthorized.
  6. `testExcessivePayload_shouldBeRejected`: Payloads exceeding maximum size bounds are rejected safely.
  7. `testSensitiveEndpoint_rateLimiting_shouldTrigger`: Sensitive endpoints enforce rate limiting limits.

### 7.2 Log Sanitization Evidence (`SecurityAuditLoggerTest`)
- 7 / 7 tests passed confirming automated redaction of Bearer tokens and payment identifiers.
- Full details documented in [`docs/SECURITY_REGRESSION_REPORT.md`](file:///d:/Zynpath/docs/SECURITY_REGRESSION_REPORT.md).

