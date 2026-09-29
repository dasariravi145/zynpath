# Zynpath Data Export Architecture ("Request My Data")

## 1. Overview
In compliance with GDPR, CCPA, and privacy-by-design standards, Zynpath provides a self-service **Request My Data** export workflow. Players can generate a comprehensive, machine-readable JSON archive of all personal data held in Zynpath systems.

---

## 2. API Contract
- **Endpoint**: `POST /api/v1/account/export`
- **Authentication**: Required (`Authorization: Bearer <sessionToken>`)
- **Response Format**: `application/json` (Schema Version 1)

---

## 3. Export Schema (Version 1)

```json
{
  "schemaVersion": 1,
  "exportedAt": 1758963400000,
  "playerProfile": {
    "playerId": "usr_941042",
    "publicZynpathId": "ZYN-849201",
    "displayName": "Alex",
    "accountType": "REGISTERED",
    "createdAt": 1758000000000
  },
  "privacySettings": {
    "profileVisibility": "PUBLIC",
    "allowZynpathIdSearch": true,
    "allowFriendRequests": true
  },
  "notificationPreferences": {
    "isFriendAlertsEnabled": true,
    "isMultiplayerAlertsEnabled": true,
    "isDailyReminderEnabled": false
  },
  "linkedProviders": [
    "GOOGLE",
    "FACEBOOK"
  ],
  "friends": [
    {
      "playerId": "usr_551021",
      "publicZynpathId": "ZYN-110293",
      "displayName": "Sam",
      "status": "ACCEPTED"
    }
  ],
  "competitiveStats": {
    "rating": 1250,
    "tier": "GOLD",
    "matchesPlayed": 42,
    "matchesWon": 28
  },
  "subscriptionTier": "PREMIUM_MONTHLY"
}
```

---

## 4. Delivery & Configuration Boundaries
- **In-App Direct Delivery**: Implemented and active. The JSON payload is generated on the backend, returned securely to the client, and displayed in an interactive copyable dialog.
- **Asynchronous Cloud Bucket / Email Dispatch**: `BLOCKED BY CONFIGURATION` in local development due to absence of production cloud storage buckets (AWS S3 / Google Cloud Storage) and transactional SMTP infrastructure.
