# Identity Provider Configuration & Environment Setup

## Overview
This document specifies the required environment configurations, credentials, and client identifiers needed to activate real Google Sign-In and Facebook Login across development and production environments.

> [!IMPORTANT]
> **Zero Secret Commits**:
> Never commit client secrets, API keys, private keys, access tokens, or refresh tokens to version control.
> Environment variables or secure secrets management systems must be used for all production credentials.

---

## 1. Google Sign-In Configuration

### Android Client Requirements
- **Web Client ID**: Needed by Android Credential Manager to request Google ID tokens.
- Add to Android resources (`res/values/strings.xml` or via Gradle `resValue`):
```xml
<string name="default_web_client_id" translatable="false">YOUR_GOOGLE_WEB_CLIENT_ID.apps.googleusercontent.com</string>
```
- **SHA-1 Fingerprint**: Must be registered in Google Cloud Console / Firebase Console for the Android package `com.zynpath.game` (and `com.zynpath.game.debug`).

### Spring Boot Backend Requirements
- The backend verifies Google ID tokens (verifying signature, issuer `https://accounts.google.com`, audience matching `GOOGLE_CLIENT_ID`, expiration `exp`, and subject `sub`).
- Environment variable:
```bash
GOOGLE_CLIENT_ID=YOUR_GOOGLE_WEB_CLIENT_ID.apps.googleusercontent.com
```
- Configured in `application.yml`:
```yaml
zynpath:
  auth:
    google:
      client-id: ${GOOGLE_CLIENT_ID:}
```

### Current Status
- Android client checks `GoogleAuthClient.isConfigured()`.
- Backend checks `GOOGLE_CLIENT_ID`.
- If unconfigured, the UI displays `[Setup Pending]` and backend returns structured `PROVIDER_UNAVAILABLE` without crashing or faking success.

---

## 2. Facebook Login Configuration

### Android Client Requirements
- **Facebook App ID**: Needed to initialize the Facebook SDK login flow.
- Add to Android resources (`res/values/strings.xml`):
```xml
<string name="facebook_app_id" translatable="false">YOUR_FACEBOOK_APP_ID</string>
<string name="facebook_client_token" translatable="false">YOUR_FACEBOOK_CLIENT_TOKEN</string>
```

### Spring Boot Backend Requirements
- The backend verifies Facebook User Access Tokens via Graph API (`/v19.0/me?fields=id,name,email`).
- Environment variables:
```bash
FACEBOOK_APP_ID=YOUR_FACEBOOK_APP_ID
FACEBOOK_APP_SECRET=YOUR_FACEBOOK_APP_SECRET
```
- Configured in `application.yml`:
```yaml
zynpath:
  auth:
    facebook:
      app-id: ${FACEBOOK_APP_ID:}
      app-secret: ${FACEBOOK_APP_SECRET:}
```

### Current Status
- Android client checks `FacebookAuthClient.isConfigured()`.
- Backend checks `FACEBOOK_APP_ID`.
- If unconfigured, the UI displays `[Setup Pending]` and backend returns structured `PROVIDER_UNAVAILABLE`.

---

## 3. Minimal Local Development Verification
To enable real provider authentication locally:
1. Copy `backend/src/main/resources/application-example.yml` to your local environment.
2. Export `GOOGLE_CLIENT_ID=...` and `FACEBOOK_APP_ID=...`.
3. Add the corresponding debug string values to `android/app/src/debug/res/values/auth_keys.xml` (which is git-ignored).
