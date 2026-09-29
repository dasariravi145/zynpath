# Zynpath Data Retention Policy

## 1. Principles
Zynpath applies strict data minimization principles across both device-local storage and backend relational databases.

---

## 2. Retention Schedules

| Data Category | Storage Location | Retention Period | Deletion Trigger |
|---|---|---|---|
| **Local Solo Progress** | Room SQLite (`zynpath_local.db`) | Until user clears data or uninstalls app | Local Guest Reset / App Data Clear |
| **Server Account Record** | PostgreSQL (`player_accounts`) | Active lifespan | Account Deletion API |
| **Active Session Tokens** | Redis / In-memory Keystore | 30 days rolling TTL | Sign-out, password change, account deletion |
| **Friend & Social Links** | PostgreSQL (`friend_relationships`) | Until unbefriended or blocked | Removed on account deletion |
| **Push Notification Tokens** | PostgreSQL / Redis | Until rotated or invalidated | Revoked on sign-out / deletion |
| **Multiplayer Completed Matches** | PostgreSQL (`match_history`) | 180 days anonymized audit | Anonymized on account deletion |
| **Purchase & Order Records** | Google Play / Billing DB | Minimum 7 years for tax & financial compliance | Retained in billing audit table (anonymized) |
| **Data Export Archives** | Transient cache | Ephemeral (delivered synchronously) | Cleared after download |

---

## 3. Financial & Legal Hold Exceptions
In accordance with applicable taxation and transaction law, raw order identifiers and receipt signatures must be retained in an encrypted financial ledger to process refunds and prevent purchase fraud. These records are segregated from the player's personal gaming profile and cannot be used for user tracking.
