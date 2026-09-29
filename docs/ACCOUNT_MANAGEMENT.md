# Zynpath Account Management & Session Lifecycle

## 1. Overview
Zynpath operates with a **guest-first, offline-first** philosophy. Players can play Solo mode completely offline without registering. When players choose to link an account (via Google Play Games or Facebook Login), their local progress seamlessly transitions to the cloud.

---

## 2. Account States

1. **Guest State (`AuthState.GUEST`)**:
   - Player is identified locally via `guest_uuid` in DataStore.
   - All progression is written to local Room database.
   - Can access all free Solo worlds, daily challenges, and settings.
2. **Authenticated State (`AuthState.AUTHENTICATED`)**:
   - Backed by server `player_accounts` record with a unique public Zynpath ID.
   - Multiple providers can be linked (e.g. Google and Facebook).
   - Sessions are managed via secure HMAC-signed bearer tokens.
3. **Session Expired (`AuthState.SESSION_EXPIRED`)**:
   - Occurs when token TTL expires or all sessions are revoked. Client displays prompt to reauthenticate without deleting local progress.

---

## 3. Account Linking & Unlinking

- **Linking**:
  - Links guest progress with provider identity via `/api/v1/auth/link`.
  - Merges local level stars, personal stats, and cosmetics into the cloud account.
- **Provider Unlinking**:
  - Supported via `DELETE /api/v1/account/providers/{provider}`.
  - **Safety Check**: A user CANNOT unlink their only sign-in method (`LAST_SIGN_IN_METHOD` error). The system prevents players from stranding their accounts.

---

## 4. Secure Sign-Out

When a user taps **Sign Out**:
1. Active session token is cleared from `KeystoreEncryptedTokenStorage`.
2. Session record is invalidated on the server via `SessionSecurityService`.
3. In-memory profile, linked providers, and friend lists are cleared.
4. Application returns to a clean **Guest mode**.
5. Server progress is safely preserved for when the player signs in again.

---

## 5. Account Switching & Isolation

When switching from Account A to Account B:
- **Entitlement Isolation**: `SubscriptionEntitlementRepository.onAccountSwitched` clears Account A's cached entitlements to prevent privilege bleeding.
- **Social Isolation**: Friends, requests, and notifications for Account A are purged from in-memory StateFlows.
- **Push Token Rotation**: Push token binding is invalidated for Account A and rebound to Account B.

---

## 6. Security Hardening & Abuse Prevention (Prompt 36)

- **Sensitive Operations Rate Limiting**: Account export (`POST /api/v1/account/export`) and account deletion (`DELETE /api/v1/account/delete`) are protected by the `SENSITIVE` rate-limiting policy (max 5 requests/hour per player), preventing denial-of-service or export flooding.
- **Resource Ownership Verification**: Ownership verification is enforced at the controller entrypoint (`ResourceAuthorizationService.verifyOwnership(targetPlayerId)`). Player identity is derived directly from the authenticated `SecurityContext`; client-supplied account IDs are never trusted blindly.
- **Account Deletion Invalidation**: Account deletion triggers atomic invalidation of all active session tokens via `SessionSecurityService`, terminates active WebSocket connections, and scrubs push notification tokens. Stale synchronization queues are purged to guarantee deleted accounts cannot be recreated by background synchronization.

---

## 7. Sign-Out & Deletion Data Recovery Safeguards (Prompt 38)
- **Halt Authenticated Background Work**: On sign-out, all pending authenticated background synchronization workers are immediately stopped, preventing unauthorized background sync.
- **Guest and Account Data Separation**: Guest progress and authenticated cloud progress maintain independent data partitions. Guest sessions cannot access cached recovery data from previous authenticated users.
- **Deleted Account Protection**: A deleted account cannot be revived from stale local device data. Room sync operation queues for deleted accounts are purged during account deletion.

---

## 8. Google Play Compliance & Web Deletion Parity (Prompt 43)
- **Target SDK 36 Alignment**: Built and targeted for Android 16 (API 36), ensuring full compliance with Google Play's 2026 platform standards.
- **Web Deletion Portal Parity**: In compliance with Google Play's Account Deletion mandate, users can delete their accounts via the mobile client or the official standalone web portal (`assets/compliance/account_deletion_request.html`) without reinstalling the app.
- **Auto Backup Safeguard**: Android Auto Backup excludes the `AndroidKeyStore` encrypted session file (`zyn_secure_session.enc`) via `data_extraction_rules.xml`, protecting user cryptographic secrets during device migration.

