# Session Security & Credential Isolation

## Overview
Zynpath enforces strict security boundaries around application sessions, provider credentials, and token storage in compliance with **Prompt 18 Section 14, 15 & 32**.

---

## 1. Provider Credential Decoupling
- **Third-Party Provider Tokens**:
  - Google ID tokens and Facebook access tokens are treated strictly as *ephemeral exchange instruments*.
  - They are passed over TLS to the backend `/api/v1/auth/exchange` or `/api/v1/auth/link` endpoint for validation.
  - The backend never stores raw third-party access tokens as permanent session credentials.
- **Zynpath Application Session Tokens**:
  - Upon successful provider validation, the backend generates an independent cryptographically secure session token (`zyn_<32-byte Base64URL>`).
  - Standard session TTL: 30 days (configurable via `SESSION_TTL_SECONDS`).
  - Sessions can be explicitly invalidated on the server via `POST /api/v1/auth/signout`.

---

## 2. Android Client Secure Token Storage
- Implemented via `SecureTokenStorage` and `KeystoreEncryptedTokenStorage`.
- **Hardware-Backed Encryption**:
  - Encryption key is generated and stored inside the Android KeyStore (`AndroidKeyStore`) using `AES/GCM/NoPadding` with a 256-bit key.
  - Session tokens and metadata are encrypted before being written to an isolated, private file (`zyn_secure_session.enc`) in `context.filesDir`.
- **Absolute Privacy Rules**:
  - Tokens are **never** stored in plain-text `SharedPreferences` or `DataStore`.
  - Tokens are **never** logged to Logcat, terminal, or crash reports.
  - Tokens are **never** placed into Compose Navigation arguments.
  - Tokens are **never** exposed in Compose `UiState` models.

---

## 3. Server-Side Endpoint Authorization
- Cross-module authorization contract: `AuthenticationBoundaryService`.
- Backend endpoints require the `Authorization: Bearer <sessionToken>` header.
- The server validates the session token against active sessions:
  - Verifies token existence and cryptographic signature.
  - Checks expiration timestamp.
  - Associates request with authoritative `PlayerAccount` internal `playerId`.
- Client-supplied `playerId` in query or body parameters is **never** trusted as proof of authorization.
