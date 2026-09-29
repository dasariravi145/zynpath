# Zynpath: Unified Error Handling Architecture

## Overview
Zynpath implements a centralized, domain-driven error handling taxonomy. The goal is to isolate low-level technical exceptions within infrastructure layers while exposing structured, user-friendly, and actionable error states to presentation layers.

---

## 1. Shared Error Taxonomy

Errors are classified across three core dimensions:

### 1.1 Categories (`ErrorCategory`)
- `NETWORK`: Transient transport failures, DNS resolution errors, connection timeouts, or socket resets.
- `AUTHENTICATION`: Expired sessions, missing tokens, invalid credentials, or unauthorized token refresh requests.
- `AUTHORIZATION`: Insufficient permissions or role restrictions.
- `VALIDATION`: Malformed input payloads, invalid puzzle paths, or checkpoint constraint violations.
- `PERSISTENCE`: Database locks, disk space exhaustion (`SQLiteFullException`), or corrupted records.
- `SYNC_CONFLICT`: Out-of-order sync operations, version mismatches, or concurrent updates.
- `GAMEPLAY_STATE`: Invalid session snapshots, corrupted path state, or mismatched puzzle identities.
- `MULTIPLAYER`: Match connection drops, room disconnections, or authoritative match timeouts.
- `BILLING`: Google Play purchase cancellations, network verification failures, or unverified claims.
- `ENTITLEMENT`: Expired subscription cached state, offline entitlement grace limits, or pack download issues.
- `RESOURCE`: Audio resource initialization failures, haptic engine unavailability, or cosmetic asset decode errors.
- `UNKNOWN`: Uncategorized unexpected exceptions.

### 1.2 Severity Levels (`ErrorSeverity`)
- `INFO`: Non-blocking feedback (e.g., background sync deferred).
- `WARNING`: Graceful degradation in effect (e.g., offline mode active, audio disabled).
- `ERROR`: Specific user action failed and requires attention (e.g., purchase verification failed, snapshot corrupted).
- `CRITICAL`: System-level blocker (e.g., database storage full).

### 1.3 Recovery Classification (`RecoveryType`)
- `RETRYABLE_FAILURE`: Transient network or busy database error; safe to repeat automatically or via user button.
- `USER_ACTION_REQUIRED`: Requires explicit player action (e.g., re-authenticate, free storage space).
- `PERMANENT_REJECTION`: Invalid request that must not be retried (e.g., duplicate claim, rule violation).
- `CONFIGURATION_BLOCKER`: Missing service config or unsupported device feature; fallback applied.
- `UNRECOVERABLE_LOCAL_STATE`: Corrupted snapshot or invalid local record; safe fresh start offered.

---

## 2. Domain Error Model: `ZynpathError`

```kotlin
data class ZynpathError(
    val category: ErrorCategory,
    val severity: ErrorSeverity,
    val recoveryType: RecoveryType,
    val userMessage: String,
    val technicalMessage: String? = null,
    val correlationId: String = UUID.randomUUID().toString().take(8),
    val suggestedAction: RecoveryAction = RecoveryAction.None,
    val cause: Throwable? = null
)
```

### Supported Recovery Actions
- `RecoveryAction.Retry`: Trigger idempotent re-execution.
- `RecoveryAction.SignInAgain`: Prompt for re-authentication.
- `RecoveryAction.ResumeGame`: Resume existing valid session.
- `RecoveryAction.StartFresh`: Safe restart without erasing completed level records.
- `RecoveryAction.FreeStorageSpace`: Guide user to clear device storage.
- `RecoveryAction.GoHome`: Return to safe root navigation screen.
- `RecoveryAction.Dismiss`: Acknowledge and dismiss error banner.
- `RecoveryAction.None`: No active resolution required.

---

## 3. UI Error Presentation & Deduplication

### 3.1 Design System Components
- `ZynpathErrorBanner`: Inline contextual error banner supporting severity tinting and action button.
- `ZynpathOfflineIndicator`: Ambient pill indicating offline status, showing animated pulse when reconnecting.
- `ZynpathFullscreenError`: Full-screen accessible state with support reference code (`correlationId`), primary retry button, and secondary exit action.

### 3.2 Error Deduplication (`ErrorDeduplicator`)
To prevent spamming the user across rapid Jetpack Compose recompositions, coroutine retries, or repeated network callbacks, errors are throttled using a sliding 3000ms window keyed by message and category.

---

## 4. Backend Centralized Exception Handling

The Spring Boot backend utilizes `GlobalExceptionHandler` (`@RestControllerAdvice`) to transform all exceptions into standard `ErrorResponse` entities:
- `CircuitBreakerOpenException` -> HTTP 503 (Category: `NETWORK`, Retryable: true)
- `MethodArgumentNotValidException` -> HTTP 400 (Category: `VALIDATION`, Retryable: false)
- `BadCredentialsException` -> HTTP 401 (Category: `AUTHENTICATION`, Retryable: false)
- `AccessDeniedException` -> HTTP 403 (Category: `AUTHORIZATION`, Retryable: false)
- `Exception` (catch-all) -> HTTP 500 (Category: `UNKNOWN`, Retryable: false)

Stack traces, internal server class names, and infrastructure tokens are strictly scrubbed from responses.
