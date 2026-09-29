# Zynpath Rewarded Ad Verification & Reconciliation

## 1. Verification Architecture

Zynpath employs a dual verification model to accommodate both online authenticated players and offline/guest players:

```
[AdMob Network]
       │ (1. Ad completed)
       ▼
[Mobile Client (Google Mobile Ads SDK)] 
       │ (2. onUserEarnedReward)
       ├─────────────────────────────────┐
       │ (Guest / Offline)               │ (Online Authenticated)
       ▼                                 ▼
[Local Hint Repository]           [Backend API Server]
(LOCAL_CONFIRMED, capped)         (POST /api/v1/ads/reward/verify)
                                         │
                                         ▼
                                  [Reward Verification]
                                  - Deduplicate transaction
                                  - Enforce daily limit (5)
                                  - Enforce wallet cap (10)
                                  - Match AdMob SSV Webhook
                                  (SERVER_VERIFIED)
```

---

## 2. Verification States

| Status | Definition |
|---|---|
| `LOCAL_CONFIRMED` | Ad completed on client via official SDK callback; granted locally in DataStore for guest or offline play. |
| `SERVER_VERIFIED` | Backend server validated transaction ID against AdMob SSV cryptographic signature or verified client token. |
| `PENDING_VERIFICATION` | Client reported completion, but network connectivity to backend verification endpoint is temporarily unavailable. Local credit is granted temporarily under capped allowance. |
| `REJECTED` | Duplicate transaction ID, invalid cryptographic signature, or rate/daily abuse threshold exceeded. |

---

## 3. Account Linking Reconciliation

When a guest user registers or links an account:
1. Local `reward_events` are sent to `POST /api/v1/ads/reward/reconcile`.
2. The server deduplicates events against previously recorded transaction IDs across all linked devices.
3. Legitimate unspent credits are merged into the player's cloud wallet up to the maximum wallet cap (10).
4. Fabricated, unlimited, or duplicate client credits are rejected without penalizing genuine player progress.
