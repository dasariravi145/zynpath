# Zynpath: Error Diagnostics and Privacy-Conscious Telemetry

## 1. Principles of Diagnostic Privacy

Zynpath enforces strict, privacy-preserving logging and diagnostics across Android client and backend modules:

- **Zero-PII Telemetry:** Under no circumstances are player passwords, session tokens, refresh tokens, Google Play purchase tokens, private room secret keys, or precise touch coordinates logged or transmitted.
- **Sensitive Key Filtering:** `ZynpathDiagnostics` automatically filters out blacklisted sensitive keys: `token`, `accesstoken`, `refreshtoken`, `sessiontoken`, `purchasetoken`, `password`, `secret`, `gesture`, `coordinates`, `path`.
- **Bounded Diagnostic Storage:** The local in-memory event buffer is strictly bounded to the last 50 events (`ConcurrentLinkedQueue`), preventing memory leaks and unbounded disk consumption.
- **Truncated Technical Traces:** Long error strings are clamped to a maximum length of 200 characters to prevent buffer bloat and stack trace leakage.

---

## 2. Event Model & Correlation IDs

Every diagnostic event captures structured, non-identifying telemetry:

```kotlin
data class DiagnosticEvent(
    val eventType: String,
    val correlationId: String,
    val category: ErrorCategory,
    val details: Map<String, String>,
    val timestamp: Long = System.currentTimeMillis()
)
```

### Correlation ID (`correlationId`)
- Format: `ERR-XXXXXXXX` (alphanumeric uppercase, 8 characters, generated via UUID).
- Safe for display on player-facing error banners, snackbars, and full-screen recovery dialogs.
- Enables players to quote reference codes to customer support without exposing device identifiers or player account tokens.

---

## 3. Monitored Resilience Events

The diagnostic system tracks essential failure and recovery occurrences:
1. `SESSION_RESTORATION_FAILED`: Stored session snapshot failed validation during level startup (e.g. checkpoint order, wall collision, schema mismatch).
2. `SOLO_COMPLETION_PERSISTENCE_FAILED`: Room failure during solo completion recording.
3. `DATASTORE_WRITE_FAILURE`: Disk I/O or storage exhaustion during preference mutation.
4. `WEBSOCKET_RECONNECTED`: Active multiplayer socket recovered following a transient network drop.
5. `SYNC_CONFLICT_RECONCILED`: Offline synchronization queue handled concurrent version conflict.
6. `BILLING_VERIFICATION_DEFERRED`: Subscription purchase recorded locally, awaiting backend reachability.
7. `CIRCUIT_BREAKER_TRIPPED`: Backend rate limit or circuit breaker opened on external dependency.

---

## 4. Crash Reporting Boundary & Consent Policy

If an external crash reporting provider (e.g., Firebase Crashlytics) is linked:
- It must be gated strictly by player privacy consent in `PlayerSettings` (`analyticsEnabled`).
- All custom keys attached to crash reports must pass through `ZynpathDiagnostics.sanitizeMetadata()`.
- Guest users and opted-out users operate with local diagnostic buffering only.

---

## 5. Known Limitations & Configuration Blockers

- In-memory event buffer (`ZynpathDiagnostics`) resets on app cold restart unless explicitly dumped to a sanitized diagnostic file.
- Remote telemetry requires backend network reachability; queued offline diagnostics are submitted opportunistically when online synchronization executes.
- Full verification of edge-case disk exhaustion (`SQLiteFullException`) and process death restoration is deferred to the final testing phase.
