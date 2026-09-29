# Zynpath: Failure Recovery Policy

## 1. Reliability Principle

**"A failure in one optional feature must never disable unrelated gameplay."**

Zynpath enforces strict isolation barriers across system components:
- **Audio & Haptics:** If the audio engine fails to allocate hardware buffers or haptic feedback is unsupported, sound and vibration are silently disabled. Gameplay continues uninterrupted.
- **Push Notifications:** Notification registration, channel creation, or worker failures are isolated in `NotificationWorker`. They never block local Solo or online multiplayer.
- **Analytics & Diagnostics:** Diagnostic tracking failures or disk logging drops are non-blocking.
- **Store & Ads:** Failure to load the store catalog or an incomplete rewarded video ad never disables access to free levels or existing hint balances.

---

## 2. Network & Reachability Policy

### 2.1 Connectivity vs. Reachability
The presence of an active Wi-Fi or Cellular network interface does not guarantee that the Zynpath backend API is reachable. All network interactions:
- Measure actual HTTP/WebSocket round-trip success.
- Classify transport timeouts (`SocketTimeoutException`, `UnknownHostException`) under `ErrorCategory.NETWORK`.
- Provide an ambient offline indicator without modal blocking dialogs.

### 2.2 Bounded Retries & Backoff
- Client network operations apply bounded exponential backoff with jitter:
  - Initial delay: 1,000 ms
  - Multiplier: 2.0x
  - Maximum delay: 30,000 ms
  - Max automatic retries: 3 attempts
- Non-idempotent operations (such as purchase confirmations or match forfeits) are never blindly retried without unique idempotency keys.

### 2.3 Circuit Breaker Policy
On the Spring Boot backend, external calls (payment gateways, identity verification, push delivery) are protected by `CircuitBreakerService`:
- Failure threshold: 5 consecutive failures.
- Cooldown period: 30 seconds before half-open state testing.
- When open, immediately rejects calls with `CircuitBreakerOpenException` (HTTP 503) to prevent backend thread starvation.

---

## 3. Authentication & Account Isolation Policy

### 3.1 Serialized Token Refresh
- Session expiration is intercepted at repository boundaries.
- To prevent a thundering herd of refresh requests, token refreshing is serialized using a Kotlin coroutine `Mutex`.
- If a refresh request succeeds, pending calls resume transparently.
- If a refresh request is rejected with HTTP 401/403, active credentials are removed from DataStore, background sync is paused, and the user is requested to sign in again.

### 3.2 Account Isolation
- Guest accounts and authenticated player accounts maintain separate data partitions.
- Active gameplay sessions store `ownerIdentity`. A newly signed-in user cannot restore or overwrite an in-flight session belonging to a previous guest or different account.
- When a user signs out, all pending authenticated background synchronization workers are cancelled.

---

## 4. Storage & Database Safety Policy

### 4.1 Non-Destructive Migrations
- Under no circumstances is `fallbackToDestructiveMigration()` permitted for production database upgrades.
- Every Room schema evolution requires an explicit, verified `Migration` (e.g., `MIGRATION_10_11`).
- If an unknown database migration error occurs, the database file is sealed and reported; user records are not deleted.

### 4.2 Storage Full Handling
- When disk space is exhausted (`SQLiteFullException`), writes fail gracefully and are caught by `ErrorClassifier`.
- The system presents a `RecoveryAction.FreeStorageSpace` alert explaining that progress cannot be saved until device storage is freed.
- The UI never falsely claims progress was saved when Room aborts a transaction.
