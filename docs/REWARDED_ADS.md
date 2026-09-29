# Zynpath Rewarded Ads Architecture & Specification

## 1. Executive Summary

Zynpath integrates an optional, privacy-conscious rewarded advertising system powered by the official **Google Mobile Ads SDK** (`play-services-ads:23.6.0`). Rewarded ads allow free players who have exhausted their standard free hint allowance to optionally earn additional Solo hint credits without payment.

In accordance with Zynpath's core monetization principles:
- **No Forced Ads:** There are zero interstitial, banner, or pop-up ads anywhere in Zynpath. Active puzzle drawing, competitive matches, and daily challenges are never interrupted.
- **Fair Monetization:** Ads only offer non-competitive Solo hint rewards. Ads never provide competitive hints, matchmaking advantages, time extensions, or rating boosts.
- **Premium Ad Suppression:** Premium subscribers (`AD_FREE` entitlement) have all ad preloading and presentation completely suppressed.
- **Offline & Guest First:** Offline gameplay and guest players retain local hint functionality. Local rewards are capped and tracked idempotently.

---

## 2. Ad Provider & SDK Integration

### 2.1 Dependencies
- **SDK:** `com.google.android.gms:play-services-ads:23.6.0`
- **Application ID:** Configured in `AndroidManifest.xml` via `com.google.android.gms.ads.APPLICATION_ID`.
  - Debug default: `ca-app-pub-3940256099942544~3347511713` (Google official test App ID)
  - Production: Configurable via Gradle build property / CI secret.

### 2.2 Ad Unit Configuration
- **Rewarded Ad Unit ID:**
  - Debug / Development: `ca-app-pub-3940256099942544/5224354917` (Google official test rewarded ad unit)
  - Production: Configurable via `ADMOB_REWARDED_AD_UNIT_ID`.
  - Production Status: Marked **`BLOCKED BY CONFIGURATION`** until actual publisher production unit IDs are provisioned in the Play Console / AdMob account. Live production ads are strictly forbidden in debug builds.

---

## 3. Explicit Lifecycle States

The ad integration exposes an explicit, reactive state machine through `RewardedAdRepository.adState`:

| State | Description |
|---|---|
| `NOT_INITIALIZED` | SDK not yet initialized or pending configuration. |
| `CONSENT_REQUIRED` | User consent must be gathered before loading ads. |
| `LOADING` | An ad request is in flight. |
| `READY` | Rewarded ad is cached and ready to display. |
| `SHOWING` | Fullscreen ad activity is currently active on screen. |
| `REWARD_EARNED` | User satisfied full view requirements; SDK triggered reward listener. |
| `DISMISSED_WITHOUT_REWARD` | Ad closed prematurely by the user; no reward granted. |
| `UNAVAILABLE` | Ad failed to load or inventory is exhausted. |
| `ERROR` | Internal failure during loading or presentation. |

**Critical Guarantee:** Ad dismissal alone is *never* treated as proof of completion. Rewards are granted exclusively upon execution of the official `OnUserEarnedRewardListener`.

---

## 4. Server-Side Verification (SSV)

For connected accounts, AdMob Server-Side Verification callbacks are supported via:
- Endpoint: `GET /api/v1/ads/ssv-callback`
- Webhook parameters: `ad_network`, `ad_unit`, `reward_amount`, `reward_item`, `timestamp`, `transaction_id`, `user_id`, `signature`, `key_id`.
- Client verification: `POST /api/v1/ads/reward/verify`
- Deduplication: Handled idempotently by `transaction_id` in `admob_ssv_records` and `event_id` in `reward_events`.

---

## 5. Abuse Prevention & Rate Limits

- **Daily Cap:** Free players can earn a maximum of **5 rewarded ads per calendar day**.
- **Wallet Cap:** Maximum **10 unspent rewarded hint credits** stored at any time.
- **Idempotency:** Reward event IDs are tracked to prevent duplicate grants from repeat callbacks or activity recreation.

---

## 6. Google Play Ads Policy Declaration (Prompt 43)

- **Console Declaration**: Declared as **"Yes, my app contains ads"** in Google Play Console App Content.
- **User-Initiated Only**: Ads are exclusively voluntary rewarded video ads requested by players for Solo hints. No forced interstitials or intrusive banner ads.
- **Target Audience Alignment**: Under our 13+ Target Audience classification, advertising follows standard AdMob developer policies without child-directed tracking dependencies.
- **Full Premium Ad Suppression**: When an account possesses an active Premium subscription, all AdMob SDK network calls, prefetching, and video loading are suppressed entirely.

---

## 7. Rewarded Ad Integration Verification (Prompt 47)

### 7.1 Test Execution Summary (`RewardedAdIntegrationTest`)
- **Suite**: `com.zynpath.backend.ads.RewardedAdIntegrationTest`
- **Total Tests**: 5 / 5 PASSED (100% Pass Rate).
- **Verified Behaviors**:
  1. `verifyReward_withValidClaim_shouldIncrementCredits`: Verifies valid reward verification increments hint credits by 1.
  2. `verifyReward_withDuplicateTransaction_shouldReject`: Rejects repeated reward claims using the same transaction ID (`REWARD_ALREADY_GRANTED`).
  3. `ssvWebhook_withMissingSignature_shouldReject`: Rejects unauthenticated/unsigned SSV webhooks with HTTP 400 Bad Request.
  4. `ssvWebhook_withValidParams_shouldProcessIdempotently`: Verifies valid SSV webhook callback is processed and logged into `admob_ssv_records`.
  5. `verifyReward_exceedingDailyLimit_shouldReject`: Rejects reward claims once the daily 5-reward limit is reached.

### 7.2 External Provider Status
- AdMob SSV and Live Ad serving: **MOCK-VERIFIED ONLY**. Live production ad credentials are not configured in test environments (`BLOCKED BY CONFIGURATION`).
- Complete report in [`docs/REWARDED_AD_TEST_REPORT.md`](file:///d:/Zynpath/docs/REWARDED_AD_TEST_REPORT.md).

