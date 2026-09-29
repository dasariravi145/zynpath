# Authentication REST API Reference

## Base Path
`/api/v1/auth`

---

## 1. Exchange Provider Credential
Exchange an external Google or Facebook token for an authoritative Zynpath application session.

- **Method**: `POST`
- **Path**: `/api/v1/auth/exchange`
- **Headers**: `Content-Type: application/json`, `Accept: application/json`
- **Request Body**:
```json
{
  "provider": "GOOGLE",
  "providerToken": "<id_token_jwt_or_access_token>",
  "guestUuid": "f47ac10b-58cc-4372-a567-0e02b2c3d479"
}
```
- **Response (200 OK)**:
```json
{
  "sessionToken": "zyn_k8F7...secure_random...",
  "playerId": "c138d827-0205-4e09-9fc6-948ff1c0a0c2",
  "publicZynpathId": "ZYN-7749-ECHO",
  "displayName": "Pathfinder",
  "accountType": "AUTHENTICATED",
  "expiresAt": 1774612345000
}
```
- **Error Responses**:
  - `401 Unauthorized`: `INVALID_PROVIDER_CREDENTIAL` or `EXPIRED_CREDENTIAL`
  - `503 Service Unavailable`: `PROVIDER_UNAVAILABLE` (e.g. `GOOGLE_CLIENT_ID` not configured on server)

---

## 2. Link Guest Account
Link an active local guest player identity to an external identity provider without losing local progress.

- **Method**: `POST`
- **Path**: `/api/v1/auth/link`
- **Headers**: `Content-Type: application/json`, `Accept: application/json`
- **Request Body**:
```json
{
  "provider": "GOOGLE",
  "providerToken": "<id_token_jwt>",
  "guestUuid": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "displayName": "Pathfinder"
}
```
- **Response (200 OK)**:
```json
{
  "sessionToken": "zyn_j29D...secure_random...",
  "playerId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "publicZynpathId": "ZYN-8921-VORT",
  "displayName": "Pathfinder",
  "accountType": "LINKED",
  "expiresAt": 1774612345000
}
```
- **Error Responses**:
  - `409 Conflict`: `ACCOUNT_LINK_CONFLICT` (Provider account is already linked to another Zynpath player)
  - `401 Unauthorized`: `INVALID_PROVIDER_CREDENTIAL`
  - `503 Service Unavailable`: `PROVIDER_UNAVAILABLE`

---

## 3. Get Authenticated Player
Fetch authoritative player record using active session token.

- **Method**: `GET`
- **Path**: `/api/v1/auth/me`
- **Headers**: `Authorization: Bearer <sessionToken>`, `Accept: application/json`
- **Response (200 OK)**:
```json
{
  "sessionToken": "zyn_...",
  "playerId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "publicZynpathId": "ZYN-8921-VORT",
  "displayName": "Pathfinder",
  "accountType": "LINKED",
  "expiresAt": 1774612345000
}
```
- **Error Responses**:
  - `401 Unauthorized`: `SESSION_EXPIRED`

---

## 4. Sign Out
Revoke active session token on server.

- **Method**: `POST`
- **Path**: `/api/v1/auth/signout`
- **Headers**: `Authorization: Bearer <sessionToken>`
- **Response (200 OK)**:
```json
{
  "status": "SIGNED_OUT"
}
```

---

## Standard Error Response Format
```json
{
  "errorCode": "ACCOUNT_LINK_CONFLICT",
  "message": "This GOOGLE account is already linked to another Zynpath player (ZYN-1102-KILO).",
  "timestamp": 1774612345123
}
```
