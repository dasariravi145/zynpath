# Integration Test Blockers & Environment Limitations

## Purpose
This document catalogs external dependency limitations, mock-verified subsystems, configuration dependencies, and sandbox environments required for production staging vs. local integration testing.

---

## 1. External Provider Integrations (Mock-Verified Only)

Automated testing in the local development environment relies on deterministic mock adapters and in-memory stores. The following external integrations are strictly **MOCK-VERIFIED ONLY**:

### Google Sign-In & OpenID Connect
- **Tested**: JWT parsing, claims decoding, expiration checks, audience validation, account provisioning, and guest identity linking using mock cryptographic assertions.
- **Blocker**: Live verification against Google's public JWKS (`https://www.googleapis.com/oauth2/v3/certs`) requires outbound internet connectivity and Google Cloud Console OAuth Client registration (`GOOGLE_CLIENT_ID`).
- **Production Prerequisite**: Valid client IDs configured in production environment (`app.auth.google-client-id`).

### Facebook Login (Graph API)
- **Tested**: Provider exchange and mapping of Facebook user identity tokens to `PlayerAccount`.
- **Blocker**: Live verification against `graph.facebook.com/me` requires live Facebook App ID and App Secret.
- **Production Prerequisite**: Valid Facebook app credentials configured in `app.auth.facebook-app-id` and `app.auth.facebook-app-secret`.

### Google Play Billing (Android Publisher API)
- **Tested**: Free-tier defaults, graceful configuration failure (`BLOCKED BY CONFIGURATION`), mock purchase verification, and entitlement lifecycle state transitions.
- **Blocker**: Live purchase token verification requires Google Play Developer Console API service account credentials (`GOOGLE_PLAY_CREDENTIALS_JSON`).
- **Production Prerequisite**: Google Play Developer Service Account JSON key uploaded to production secret manager.

### Google AdMob (Server-Side Verification / SSV)
- **Tested**: Signature presence verification, transaction ID deduplication, daily claim caps (5/24h), and credit balance increments.
- **Blocker**: Live cryptographic verification of Google AdMob ECDSA signatures against Google's public key server (`https://admob.google.com/`) requires live AdMob network traffic.
- **Production Prerequisite**: AdMob App ID and Ad Unit IDs registered in Google AdMob Console.

### Firebase Cloud Messaging (FCM)
- **Tested**: Graceful configuration fallback. Startup logs confirm `Firebase Cloud Messaging credentials not configured. Remote push delivery marked BLOCKED BY CONFIGURATION`.
- **Blocker**: Live remote push dispatch requires Firebase project Service Account credentials (`GOOGLE_APPLICATION_CREDENTIALS`).
- **Production Prerequisite**: Service account key provisioned in production deployment environment.

---

## 2. Infrastructure & Database Isolation

- **Test Environment**: In-memory database with Spring Boot Test container isolation. Zero production databases or external cloud infrastructure touched during execution.
- **Migration Verification**: Flyway SQL migrations `V1__init_schema.sql` through `V6__indexes_and_performance_tuning.sql` verified for syntax correctness, non-destructive DDL, table relationships, and index declarations.
- **Terminal Execution Budget**: Completed strictly within the 2-approval budget (Batch 1 inspection, Batch 2 compile and test execution).
