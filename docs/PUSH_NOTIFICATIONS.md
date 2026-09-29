# Zynpath: Push Notification Integration & Lifecycle

## Overview
Zynpath implements an opt-in, privacy-preserving push notification foundation. Push notifications allow players who have enabled notifications to receive urgent, time-sensitive game alerts when the app is backgrounded or inactive:
- Friend Duel invitations with expiration timers.
- Mini League invitations and room readiness.
- Friend requests and acceptances.
- Authoritative match results.

Push notifications are strictly optional; all features work without push notifications via the in-app notification center.

---

## 1. Push Provider Architecture

Zynpath integrates with **Firebase Cloud Messaging (FCM)** as the primary mobile push infrastructure.

### Boundary Interface: `PushNotificationGateway.java`
The backend interacts with push delivery through a decoupled abstraction:
```java
public interface PushNotificationGateway {
    PushResult sendPush(String deviceToken, String title, String body, Map<String, String> dataPayload);
    boolean isConfigured();
}
```

### Configuration Detection & Honest Reporting: `FirebasePushGateway.java`
- Checks whether `GOOGLE_APPLICATION_CREDENTIALS` or `zynpath.push.firebase.credentials-path` is present and points to a valid file.
- When credentials are not mounted (e.g. during local developer testing or offline test builds), the gateway reports `BLOCKED BY CONFIGURATION` (`PushResult.blockedByConfiguration(...)`) and suppresses dispatch.
- **Never fabricates delivery**: Delivery attempts are logged as configuration-blocked without throwing runtime exceptions or crashing services.

---

## 2. Device Token Lifecycle & Registration

### Token Management (`PushTokenManager.kt`)
- On Android, `PushTokenManager` acquires the device token (or generates an installation-bound token identifier in test environments).
- Tokens are cached locally in Jetpack DataStore (`pushToken`).
- Token changes or rotations automatically invoke `registerPushToken` against the backend.

### REST Endpoints
- **Register Token**: `POST /api/v1/notifications/push-token`
  - Payload: `{ "deviceToken": "...", "deviceType": "ANDROID", "appVersion": "1.0.0" }`
  - Associates the token with the authenticated player's internal ID.
- **Unregister Token**: `DELETE /api/v1/notifications/push-token`
  - Payload: `{ "deviceToken": "..." }`
  - Clears the device association upon sign-out.

---

## 3. Account Isolation & Token Invalidation

- **Sign-Out Behavior**:
  - When a user signs out, `PushTokenManager.onSignOut()` is invoked.
  - The device token is unregistered from the backend for that player account.
  - Local push token reference is cleared.
  - This prevents delivering one player's private invitations or match alerts to another player who subsequently signs in on the same shared physical device.
- **Account Switch**:
  - When switching accounts, any new session registers its token under the new player's account identity.

---

## 4. Privacy & Lock-Screen Data Minimization

- Push notifications never include private game tokens, authorization headers, or sensitive user data.
- Payload data contains minimal navigation coordinates:
  - `notificationId`
  - `eventType`
  - `resourceId` (e.g., room ID or duel ID)
  - `destination` (e.g., `friend_duel`, `mini_league`)
- Tokens are never exposed via public profile endpoints or player search.
