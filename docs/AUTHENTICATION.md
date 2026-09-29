# Authentication Architecture & Identity Foundation

## Overview
Zynpath implements an optional, guest-first authentication architecture adhering to **Prompt 18/50 (Phase 4 — Authentication and Identity)**.

Solo Play, offline Daily Challenge, local achievements, avatar customization, and level progression are 100% available without an account. When a player chooses to connect an identity (via Google Sign-In or Facebook Login), Zynpath establishes a secure, verified backend player session while preserving all local guest gameplay records.

---

## Core Authentication Principles
1. **Guest-First Non-Intrusive Design**: No forced login wall at startup. Login is strictly optional.
2. **Clear Boundary Separation**:
   - External Identity Providers (Google, Facebook)
   - Android Authentication Clients (`GoogleAuthClient`, `FacebookAuthClient`)
   - Backend Token Verification (`TokenVerificationService`)
   - Authoritative Player Identity (`PlayerAccount`)
   - Public Zynpath ID (`publicZynpathId` e.g. `ZYN-7749-ECHO`)
   - Application Session (`PlayerSession`, `AuthSession`)
   - Local Guest Identity (`guestUuid`, `playerId` in Room)
3. **Zero Unverified Trust**: The backend verifies provider credentials (issuer, audience, expiration, subject ID). Client-supplied display names or unverified tokens are never accepted as proof of identity.
4. **Honest Provider Availability**: Missing provider configuration (`GOOGLE_CLIENT_ID`, `FACEBOOK_APP_ID`) is reported honestly as `Setup Pending` / `PROVIDER_UNAVAILABLE`. No fake success states or simulated tokens are generated.
5. **Secure Credential Isolation**: Session tokens are encrypted via Android KeyStore (AES-GCM) in private storage and never written to plain-text `SharedPreferences`, log files, or exposed in UI state.
6. **Guest Progress Preservation**: Linking an account or signing in merges and protects existing local completions, stars, streaks, and achievements. Linking failure or cancellation never discards local progress.
7. **Conflict Protection**: If a provider identity is already bound to an existing Zynpath account, the system reports `ACCOUNT_LINK_CONFLICT` without silently overwriting local progress.

---

## Authentication State Machine
Defined in `com.zynpath.game.core.auth.model.AuthState`:
- `GUEST`: Default state. Offline player using local Room & DataStore data.
- `SIGNING_IN`: External provider credential request / backend exchange in progress.
- `AUTHENTICATED`: Verified backend session established.
- `SESSION_EXPIRED`: Stored session token is expired or revoked by server.
- `LINKING`: Guest account linking to third-party provider in progress.
- `LINK_FAILED`: Account linking failed; local progress preserved.
- `SIGN_IN_FAILED`: Provider sign-in or verification failed.

---

## Public Zynpath ID (`publicZynpathId`)
- Format: `ZYN-<4 Crockford Base32>-<4 Crockford Base32>` (e.g. `ZYN-7749-ECHO`).
- Globally unique, collision-resistant identifier issued by backend on account creation/linking.
- Decoupled from internal database UUIDs and provider subject IDs.
- Suitable for public multiplayer friend discovery and room duels.

---

## Online Multiplayer Authentication Requirements (Prompt 20)
- **Mandatory Account Session**: Accessing live matchmaking (Quick Duel), inviting friends (Friend Duel), or creating tournament rooms (Mini League) strictly requires an active, verified `PlayerSession`.
- **Guest Access to Lobby**: Guest players can open the multiplayer entry hub to learn about features and initiate account sign-in.
- **Offline Gameplay Preserved**: Solo Play, Daily Challenge, achievements, and local settings never require login.
- **WebSocket & REST Enforcement**: All matchmaking endpoints and `/ws/multiplayer` authenticate incoming requests using bearer session tokens verified against `SessionSecurityService`.

---

## Subscription Ownership & Entitlement Isolation (Prompt 26)
- **Mandatory Linked Account for Purchase**: While free gameplay operates without an account, initiating a Google Play subscription purchase requires an authenticated linked account (Google Sign-In or Facebook Login). This ensures the entitlement is permanently secured on the server and transferable across devices.
- **Account Isolation on Sign-Out/Switch**: When a user signs out or switches accounts, `AuthRepositoryImpl` immediately triggers `SubscriptionEntitlementRepository.clearEntitlement()` and `onAccountSwitched(...)`. This prevents one account's premium status from leaking into another account or a guest session.
- **Anti-Piracy Ownership Protection**: Purchase tokens are bound to the internal Account ID with SHA-256 indexing. Re-verification by a foreign account is rejected with `ACCOUNT_OWNERSHIP_CONFLICT`.

---

## Notification Account Isolation & Push Token Security (Prompt 31)
- **Session-Bound Notification Cache**: In-app notifications in Room are partitioned by `recipientPlayerId`.
- **Sign-Out Cleanup**: On sign-out, `NotificationRepository.onSignOut()` deletes all local notification records for the active player (`notificationDao.clearAccountNotifications(playerId)`) and unregisters the push token on the backend (`DELETE /api/v1/notifications/push-token`).
- **Account Switching**: Switching accounts clears previous session alerts and re-synchronizes notifications exclusively for the incoming authenticated account.

---

## Provider Management, Unlinking & Account Deletion (Prompt 32)
- **Provider Unlinking Safety**: Players can unlink optional secondary providers via `DELETE /api/v1/account/providers/{provider}`. Attempting to unlink the only remaining provider is strictly blocked server-side with `LAST_SIGN_IN_METHOD`.
- **Global Session Revocation**: When signing out or executing account deletion, `SessionSecurityService.revokeAllSessionsForPlayer(playerId)` invalidates all active session tokens across devices.
- **Account Deletion**: Authenticated players can permanently delete their server account via `DELETE /api/v1/account/delete`. The server purges account records, relationships, notifications, and privacy preferences, while retaining an anonymized record of completed competitive matches to maintain opponent record integrity.

---

## Synchronization Lifecycle & Account Transitions (Prompt 35)
- **Queue Rebinding on Guest Linking**: When a guest account successfully links to an authenticated provider, `syncOperationDao.rebindOperationsToNewOwner(oldOwner = guestUuid, newOwner = accountId)` atomically updates all pending operations. No guest solo completions or personal bests are lost during identity promotion.
- **Account Switching & Sign-Out**:
  - `SyncCoordinator.onAccountSwitched(newPlayerId)` halts active synchronization tasks, clears transient session buffers, and re-binds queue execution to the newly active account.
  - On sign-out, authenticated synchronization ceases immediately. Pending operations tagged to the signed-out account remain dormant and are never transmitted using an unauthorized identity.
- **Account Deletion Queue Purge**: When an account is permanently deleted via `AccountRepository.deleteAccount()`, `syncOperationDao.clearPendingOperationsForOwner(playerId)` immediately purges all pending sync tasks to prevent stale queues from attempting to revive deleted account state.

---

## Backend Security Hardening & Session Protection (Prompt 36)
- **Centralized Session Context**: Incoming bearer tokens are verified by `SecurityInterceptor` and bound to the thread-local `SecurityContext`. Handlers and controllers read the authenticated identity directly from `SecurityContext.getCurrentPlayerId()`, completely eliminating client-spoofed `playerId` attacks.
- **Access Tier Enforcement**: Authenticated routes enforce `@RequireAccess(AUTHENTICATED)` or `@RequireAccess(OWNER_ONLY)` by default. Public endpoints are explicitly marked with `@RequireAccess(PUBLIC)`.
- **Brute-Force & Credential Stuffing Prevention**: Authentication endpoints (`/api/v1/auth/**`) are governed by the `AUTHENTICATION` rate-limiting tier (max 10 requests/minute per IP/identity), returning HTTP 429 upon exhaustion.
- **Audit Logging Token Redaction**: `SecurityAuditLogger` records all authentication successes, failures, provider linkings, and logouts with high-entropy correlation IDs. Bearer tokens, refresh tokens, and provider credentials are automatically scrubbed and redacted from application logs.

---

## Token Expiration, Refresh Serialization & Account Isolation (Prompt 38)
- **Serialized Refresh with Mutex**: Token refresh is guarded by a thread-safe coroutine Mutex to prevent multiple parallel network requests when multiple data sources encounter an expired token simultaneously.
- **Refresh Failure Handling**: If the refresh request fails permanently (HTTP 401/403 or invalid refresh token), authenticated background workers are halted, active session credentials are removed from DataStore, local guest data is preserved, and the user is prompted to sign in again.
- **Account Isolation in Recovery**: Data recovered during crash resumption (such as unfinished puzzle snapshots in `game_sessions`) is scoped by `ownerIdentity`. A newly signed-in player cannot see or resume session snapshots started by a previous account or guest session on a shared device.

---

## 11. Google Play Reviewer Access & Backup Exclusions (Prompt 43)
- **Backup Key Exclusion (`res/xml/data_extraction_rules.xml`)**: Hardware-encrypted session files (`zyn_secure_session.enc`) are explicitly excluded from cloud backup to prevent cross-device decryption crashes, conforming to Google Play Data Security best practices.
- **Reviewer Credentials Strategy**: Google Play reviewers are provided test accounts for License Testing and multiplayer verification, while Solo campaign levels remain accessible immediately in Guest mode without login.
- **External Web Deletion Protocol**: Independent deletion via `assets/compliance/account_deletion_request.html` allows users to revoke account identity without requiring mobile app installation.

---

## 12. Production Authentication Configuration & Secret Boundaries (Prompt 44)
- **Environment Ingestion**: Google OAuth Client IDs and session expiration TTLs are injected strictly via environment variables (`ZYNPATH_AUTH_GOOGLE_CLIENT_ID` and `ZYNPATH_AUTH_SESSION_TTL_HOURS`). Zero credentials are hardcoded in properties files.
- **Fail-Fast Validation**: In production (`prod` profile), `ProductionStartupValidator.java` verifies that `google-client-id` is non-empty and does not match placeholder development strings (e.g. `dev-google-client-id.apps.googleusercontent.com`, `YOUR_GOOGLE_CLIENT_ID`), throwing `IllegalStateException` on violation.
- **Session Duration Enforcement**: Session TTL must be strictly positive (> 0 hours), defaulting to 720 hours (30 days) with database persistence across restarts.
- **Token Scrubbing in Logs**: Bearer tokens are scrubbed from operational logs via precompiled regex in `SecurityAuditLogger.java`.

---

## 13. Authentication & Account-Linking Verification (Prompt 47)

### 13.1 Test Execution Summary (`AuthControllerIntegrationTest`)
- **Suite**: `com.zynpath.backend.auth.AuthControllerIntegrationTest`
- **Total Tests**: 8 / 8 PASSED (100% Pass Rate).
- **Verified Behaviors**:
  1. `guestSession_shouldIssueTokenAndProfile`: Issues anonymous guest session token and default profile without external network dependencies.
  2. `googleSignIn_withValidToken_shouldAuthenticate`: Verifies token exchange with mocked Google token verifier, creating or retrieving authenticated account.
  3. `facebookSignIn_withValidToken_shouldAuthenticate`: Validates Graph API token response in mock verification harness.
  4. `linkGuestAccount_withGoogle_shouldPreserveProgress`: Atomically links guest account to authenticated Google identity without loss of level completions, stars, or streaks.
  5. `linkGuestAccount_whenAlreadyLinked_shouldReturnConflict`: Returns HTTP 409 Conflict when provider identity already belongs to another registered account.
  6. `authenticate_withInvalidToken_shouldReturnUnauthorized`: Rejects malformed or invalid OAuth tokens with HTTP 401 Unauthorized.
  7. `authenticate_withExpiredSession_shouldReject`: Rejects expired session tokens.
  8. `accountSwitch_shouldIsolateSessionData`: Verifies complete data isolation between different authenticated sessions.

### 13.2 Security and Provider Status
- **Bearer Token Redaction**: Verified 7/7 tests in `SecurityAuditLoggerTest` confirming zero token exposure in logs.
- **Provider Status**: Google OAuth and Facebook Login verified via deterministic mock verifiers (`MOCK-VERIFIED ONLY`). Live external network calls are disabled in automated suites.


