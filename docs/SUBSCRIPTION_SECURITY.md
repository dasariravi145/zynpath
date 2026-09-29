# Zynpath Subscription Security & Anti-Fraud Architecture

**Application:** Zynpath: Number Path Puzzle  
**Phase:** 6 — Monetization and Premium (Prompt 26/50)

---

## 1. Threat Model & Security Principles

Digital subscription systems are common targets for reverse-engineering, receipt replay attacks, token theft, and account spoofing. Zynpath adheres to strict security standards to ensure complete financial and entitlement integrity:

| Threat | Mitigation Architecture |
|---|---|
| **Client-side Tampering (Modded APK)** | Client state is completely non-authoritative. Features are validated on the backend. |
| **Receipt / Token Replay** | Tokens are bound to the internal account ID with cryptographic SHA-256 hash indexing. |
| **Cross-Account Token Theft** | Submitting an already bound purchase token from an unrelated account is rejected with HTTP 409. |
| **Sensitive Credential Leakage** | Raw purchase tokens are never logged, never sent to analytics, and never displayed in UI. |
| **Fabricated Verification** | Backend uses official Google Play Developer API and reports `BLOCKED BY CONFIGURATION` if credentials are unset. |

---

## 2. Purchase Token Protection

- **Transmission:** Purchase tokens are transmitted solely over HTTPS in the body of authenticated `POST /api/v1/subscription/verify` requests.
- **Client Storage:** The Android client does not store purchase tokens in plain-text DataStore or SharedPreferences.
- **Backend Storage:** In `SubscriptionService.java`, purchase tokens are hashed using SHA-256 (`hashToken(purchaseToken)`) for indexing in memory and database records. The raw token is retained only for API communication with Google Play.
- **Logging Sanitization:** All billing repository and backend logs mask or omit token strings.

---

## 3. Account Ownership & Anti-Piracy

### Obfuscated Account Identifier

When launching the Google Play purchase flow, Android passes an obfuscated hash of the internal Zynpath Account ID:

```kotlin
val flowParams = BillingFlowParams.newBuilder()
    .setProductDetailsParamsList(paramsList)
    .setObfuscatedAccountId(obfuscateAccountId(accountId))
    .build()
```

### Ownership Binding & Conflict Prevention

On the backend:
1. When a verification request arrives, the token hash is looked up.
2. If the token hash exists and is bound to a different `accountId`:
   ```java
   if (!existing.accountId().equals(accountId)) {
       throw new OwnershipConflictException("PURCHASE_OWNERSHIP_CONFLICT: Token already bound to another account");
   }
   ```
3. If the account matches, the existing record is updated with the fresh verification timestamp.
4. If the token is new, it is permanently bound to the calling account.

---

## 4. Honest Configuration Boundary

In adherence to Prompt 26 instructions:
- The system **never fabricates purchase success** or fakes subscription status.
- When Google Play Developer API service-account credentials (`GOOGLE_PLAY_SERVICE_ACCOUNT_KEY_PATH`) are missing from the backend environment, the backend responds honestly with `BLOCKED BY CONFIGURATION` (HTTP 503).
- In local development or testing without Google Play credentials, test tokens can be supplied through authorized mock flags when explicitly enabled, but production pathways remain strictly protected.
