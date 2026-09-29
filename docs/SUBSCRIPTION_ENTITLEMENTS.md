# Zynpath Subscription Entitlements & Authoritative Backend Model

**Application:** Zynpath: Number Path Puzzle  
**Phase:** 6 — Monetization and Premium (Prompt 26/50)

---

## 1. Authoritative Backend Model

Subscription status is strictly server-authoritative. The client application never determines its own entitlement through local state manipulation or unverified flags.

### Entitlement Entity (`SubscriptionEntitlement`)

```java
public record SubscriptionEntitlement(
    String accountId,
    EntitlementStatus status,
    String productId,
    String basePlanId,
    long currentPeriodEndMs,
    long lastVerifiedAtMs,
    boolean isAutoRenewing,
    String purchaseTokenHash,
    List<String> unlockedFeatureKeys
) {
    public boolean isActive() {
        if (!status.isEntitled()) return false;
        if (currentPeriodEndMs <= 0) return true;
        return System.currentTimeMillis() <= currentPeriodEndMs;
    }
}
```

### Entitlement States Lifecycle

| State | Definition | Premium Features Unlocked? | Action Required |
|---|---|---|---|
| `FREE` | Default tier for non-subscribers | No | None (Standard gameplay) |
| `ACTIVE` | Verified active subscription | **Yes** | Automatic renewal |
| `IN_GRACE_PERIOD` | Payment failed; Play Store in grace period retry | **Yes** | Notice shown in banner |
| `ON_HOLD` | Grace period ended without payment; subscription paused | No | Redirect to Play Store |
| `PAUSED` | User intentionally paused subscription | No | Resume via Play Store |
| `CANCELLED_BUT_ACTIVE` | User cancelled renewal; paid period still active | **Yes** | Active until `currentPeriodEndMs` |
| `EXPIRED` | Paid period ended without renewal | No | Re-subscribe |
| `REVOKED` | Refunded, revoked by Google Play, or chargeback | No | Returned to free tier |
| `UNKNOWN` | Verification status inconclusive | No | Safe fallback to free tier |

---

## 2. API Contract

Base Path: `/api/v1/subscription`  
Authentication: `Authorization: Bearer <sessionToken>`

### Endpoints

#### 1. Retrieve Current Entitlement
- **Method:** `GET /entitlement`
- **Response:** `200 OK`
  ```json
  {
    "status": "ACTIVE",
    "productId": "zynpath_premium",
    "basePlanId": "premium-six-months",
    "active": true,
    "gracePeriod": false,
    "expiresAt": 1780000000000,
    "lastVerifiedAt": 1758800000000,
    "accountId": "zyn_acc_12345",
    "message": "Authoritative entitlement retrieved."
  }
  ```

#### 2. Submit Purchase for Verification
- **Method:** `POST /verify`
- **Request Body:**
  ```json
  {
    "purchaseToken": "<sensitive_play_purchase_token>",
    "productId": "zynpath_premium",
    "basePlanId": "premium-monthly",
    "obfuscatedAccountId": "zyn_acc_12345"
  }
  ```
- **Responses:**
  - `200 OK`: Entitlement response with status `ACTIVE`.
  - `409 CONFLICT`: `{"message": "PURCHASE_OWNERSHIP_CONFLICT: Token already bound to another account"}`
  - `503 SERVICE_UNAVAILABLE`: When Google Play Developer API is not yet configured (honestly reporting `BLOCKED_BY_CONFIGURATION`).

#### 3. Refresh Subscription State
- **Method:** `POST /refresh`
- **Response:** `200 OK` with freshly verified entitlement data.

#### 4. Restore Subscriptions
- **Method:** `POST /restore`
- **Request Body:** `{"purchaseTokens": ["<token1>", "<token2>"]}`
- **Response:** `200 OK` with restored active entitlement or free fallback.

---

## 3. Bounded Offline Cache Policy

To ensure players retain offline access to their paid benefits (such as Solo hints and premium packs during flights or network dropouts):
1. Verified entitlements are cached locally in `zyn_entitlement_cache.json`.
2. Access remains valid while:
   - `System.currentTimeMillis() <= currentPeriodEndMs`, OR
   - Maximum 7 days from `lastVerifiedAtMs` if no explicit expiration is provided.
3. Cache entries are marked with `isCachedOffline = true`.
4. If the cache expires while offline, the app safely defaults to `FREE` status until reconnected, **without interrupting offline free gameplay**.

---

## 4. Account Switching & Isolation

When the authenticated account changes:
1. `AuthRepositoryImpl` calls `SubscriptionEntitlementRepository.onAccountSwitched(newAccountId)`.
2. Existing cached entitlement files are immediately cleared.
3. The UI state is reset to `FREE` for the new session.
4. An authoritative refresh is triggered for the new account identity.
5. On sign-out, `clearEntitlement()` guarantees no premium leakage into the guest session.

---

## 5. Content Access Gating & Premium Solo Packs (Prompt 27)

- **Backend Enforcement**: Backend endpoint `GET /api/v1/content/packs/{packId}/download` verifies active `SubscriptionEntitlement` before transmitting pack puzzle payloads.
- **Client Offline Authorization**: `PremiumPackRepository.resolveAccessStatus(packId)` checks active `SubscriptionEntitlement` and local installation state.
- **Offline Bounded Window**: Previously downloaded and authorized content remains playable offline up to 30 days while local cached entitlement is valid.
- **Safe Suspension**: When entitlement expires, gameplay access locks gracefully with a prompt to renew subscription, while locally stored puzzle files and completed progress are strictly preserved.

---

## 6. Cosmetic Entitlement Enforcement & Public Profile Sync (Prompt 28)

- **Cosmetic Feature Keys**: Authoritatively maps to feature keys:
  - `PREMIUM_THEMES`: Unlocks non-standard visual color themes (Solar Sunset, Cyber Neon).
  - `PREMIUM_PATH_EFFECTS`: Unlocks animated and trail path effects (Gentle Pulse, Gradient Trail, Particle Accents).
  - `PREMIUM_AVATAR_FRAMES`: Unlocks decorative avatar frames (Silver Outline, Gold Accent, Neon Ring).
- **Authoritative Equipment Validation**: `POST /api/v1/cosmetics/equipped` validates active entitlement on the Spring Boot backend before persisting public selections. Unauthorized items are rejected with HTTP 403 `ENTITLEMENT_REQUIRED`.
- **Public Profile Isolation**: `GET /api/v1/cosmetics/public/{playerId}` returns strictly public cosmetic identifiers (`themeId`, `pathEffectId`, `avatarFrameId`). Billing account IDs, purchase tokens, payment methods, and expiration timestamps are never exposed.
- **Resubscription Auto-Restoration**: When an expired account resubscribes, previously saved premium selections are automatically validated against the catalog and restored to active rendering.

---

## 7. Rewarded Ads & Hint Credit Reconciliation (Prompt 29)

- **Authoritative Gating**: `FeatureAccessPolicy.isFeatureUnlocked(PremiumFeatureKey.AD_FREE, null, entitlement)` governs ad suppression. When active, all ad requests and UI prompts are suppressed.
- **Backend SSV Verification**: Endpoints under `/api/v1/ads/reward/*` verify AdMob Server-Side Verification (SSV) webhooks and client verification claims, binding reward events to authenticated accounts idempotently.
- **Account Linking Credit Reconciliation**: When an offline guest with earned local hint credits links an account, credits are merged into the account wallet up to the max wallet cap (10) after deduplicating transaction IDs against previously verified events.

---

## 8. Account Deletion & Subscription Decoupling (Prompt 32)
- **Play Store Subscription Decoupling**: Deleting a Zynpath account purges server gaming records, but Google Play Store subscriptions are managed separately by Google Play. The app displays an explicit reminder that players must cancel active subscriptions in the Google Play Store.
- **Entitlement Teardown**: Upon account deletion, the local entitlement cache is erased immediately (`clearEntitlement()`), preventing unauthorized offline entitlement retention.

---

## 9. Sync Pipeline & Entitlement Isolation (Prompt 35)
- **Zero Local Elevation**: The offline synchronization pipeline (`SyncCoordinator`, `sync_operations`) is strictly decoupled from billing logic. Queued operations cannot mint, alter, or extend subscriptions.
- **Reconciliation Authority**: Premium entitlement changes flow strictly downstream from Google Play Billing and the backend `EntitlementService` into the local client cache.
- **Progress Preservation across Expiration**: If premium entitlement lapses during an offline period, user progress, puzzle pack completion records, and personal bests remain safely stored in Room, while gated levels simply request subscription renewal upon attempt.

---

## 10. Security Hardening & Billing Integrity (Prompt 36)

- **Purchase Token Binding & Anti-Piracy (`BillingIntegrityGuard`)**:
  - Validates purchase tokens server-side and indexes their SHA-256 hash against the authenticated player account ID.
  - Re-verification of a purchase token already associated with a different account is rejected immediately with HTTP 409 `PURCHASE_OWNERSHIP_CONFLICT`.
- **Deduplication of Ad Rewards**:
  - Rewarded ad transactions are validated through `BillingIntegrityGuard.validateRewardTransaction`.
  - Transaction IDs are recorded atomically; duplicate callbacks or replay attacks are rejected with `REWARD_ALREADY_GRANTED`.
- **Purchase Token Redaction**:
  - Purchase tokens, credit card metadata, and raw Play Store credentials are never written to application logs.
  - Audit events (`ENTITLEMENT_VERIFIED`, `REWARD_GRANTED`) recorded by `SecurityAuditLogger` log only masked token fingerprints (`sha256(token).substring(0, 8)...`).

---

## 11. Billing & Entitlement Failure Recovery (Prompt 38)
- **Billing Failure Isolation**: Failures during Google Play Billing interaction (purchase cancellation, network timeout, verification pending) never crash the app or interrupt free gameplay.
- **No Unverified Premium Grants**: Premium benefits are never granted from unverified client claims. Verification failures prompt the user to retry restoration (`Restore Purchases`) without unlocking features prematurely.
- **Bounded Offline Entitlement**: Offline grace periods are strictly capped at 30 days. Backend outages or long offline periods do not create indefinite free access.
- **Rewarded Ad Failure Safety**: If a rewarded video ad fails to load or the player dismisses before completion, no reward credits are minted and no hint balances are consumed.

---

## 12. Google Play Monetization Compliance & Policy Audit (Prompt 43)
- **Billing Library 7.1.1**: Integrated via `com.android.billingclient:billing-ktx:7.1.1` without third-party external checkout links.
- **Planned vs. Live Pricing**: ₹99/month and ₹499/6-months are planned base prices; exact localized prices are retrieved dynamically via `BillingClient.queryProductDetailsAsync`.
- **Zero Competitive Pay-to-Win Advantage**: Premium subscribers receive unlimited hints exclusively in Solo campaign levels. Hints are strictly prohibited and disabled in all competitive multiplayer modes (Quick Duel, Friend Duel, Mini League).
- **Cancellation Transparency**: Subscription screens and account deletion dialogs explicitly direct players to `play.google.com/store/account/subscriptions`.

---

## 13. Production Database Schema & Purchase Verification Security (Prompt 44)
- **Authoritative Entitlement Persistence (`V1` & `V4`)**:
  - `subscription_entitlements` stores purchase tokens with unique constraints (`purchase_token_hash`), preventing token reuse across accounts.
  - `player_cosmetics` and `player_hint_balances` record unlocked themes, avatar frames, and earned hints.
  - `reward_events` and `admob_ssv_records` ensure idempotent SSV ad reward validation.
- **Google Play Service Account Boundary**: Backend purchase verification uses Google Play Developer API via service account credentials configured securely by `GOOGLE_APPLICATION_CREDENTIALS`. Credentials are never committed or exposed to the client.
- **Strict Verification Gating**: When Google Play Developer API is unreachable, the backend returns a transient verification error (`VERIFICATION_UNAVAILABLE`). Premium features are NEVER granted on unverified claims.

---

## 14. Backend Billing & Entitlement Verification (Prompt 47)

### 14.1 Test Execution Summary (`SubscriptionIntegrationTest`)
- **Suite**: `com.zynpath.backend.billing.SubscriptionIntegrationTest`
- **Total Tests**: 5 / 5 PASSED (100% Pass Rate).
- **Verified Behaviors**:
  1. `verifyPurchase_withValidGoogleToken_shouldGrantEntitlement`: Verifies valid purchase token validation with mock Play Developer API, activating `ACTIVE` subscription entitlement.
  2. `verifyPurchase_withExistingTokenOnDifferentAccount_shouldReturnConflict`: Enforces token anti-piracy, rejecting attempts to reuse a token belonging to another account with HTTP 409 Conflict.
  3. `verifyPurchase_withExpiredOrRevokedToken_shouldReject`: Rejects invalid, expired, or fraudulent purchase tokens without granting access.
  4. `getEntitlementStatus_shouldReturnCurrentState`: Accurately returns status (`ACTIVE`, `EXPIRED`, `CANCELLED_BUT_ACTIVE`) and current period timestamps.
  5. `duplicateVerification_shouldBeIdempotent`: Duplicate webhook or verification calls do not duplicate entitlement records or reset subscription periods.

### 14.2 External Provider Status
- Google Play Developer API verification: **MOCK-VERIFIED ONLY**. Real Google Play Console service account credentials are not configured in automated test environments (`BLOCKED BY CONFIGURATION`).
- Complete execution details recorded in [`docs/BILLING_TEST_REPORT.md`](file:///d:/Zynpath/docs/BILLING_TEST_REPORT.md).



