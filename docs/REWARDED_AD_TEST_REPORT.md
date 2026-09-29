# Rewarded Ad Verification Test Report

## Overview
Verification of Google AdMob rewarded ad completion verification, credit balance tracking, anti-replay fraud prevention via `BillingIntegrityGuard`, 24-hour daily reward caps (5/day), and AdMob Server-Side Verification (SSV) webhook handling.

- **Suite**: `com.zynpath.backend.ads.RewardedAdIntegrationTest`
- **Tests Executed**: 5
- **Passed**: 5
- **Failed**: 0
- **Status**: **PASSED** (AdMob Live Network: **MOCK-VERIFIED ONLY**)

---

## Detailed Results

| Test Method | Category | Verified Behavior | Status |
| :--- | :--- | :--- | :--- |
| `verifyReward_validReward_grantsHintCredit` | Reward Grant | Authenticated player submits valid rewarded ad claim; server verifies parameters, increments balance by +1, and returns `SERVER_VERIFIED` status. | **PASSED** |
| `getBalance_returnsCurrentBalance` | Balance Query | Returns player's available free hints (3), earned rewarded credits (1), total available (4), and daily remaining ad claims (4). | **PASSED** |
| `verifyReward_duplicateEventId_isIdempotent` | Anti-Replay Guard | Submitting the same `rewardEventId` twice is intercepted by `BillingIntegrityGuard.assertRewardTransactionUnique`, rejecting duplicate grant attempts with `400 Bad Request`. | **PASSED** |
| `verifyReward_exceedsDailyLimit_isRejected` | Abuse Control | After 5 rewards in 24 hours, the 6th claim is rejected with `400 Bad Request` and `Daily rewarded ad limit reached (5/5)`. | **PASSED** |
| `handleSsvCallback_returnsOk` | AdMob SSV Webhook | Incoming AdMob SSV callback with valid transaction ID, cryptographic signature, and key ID is verified and responded to with HTTP 200 `OK`. Missing signature is rejected with HTTP 400. | **PASSED** |

---

## Reward Limits & Guardrails

- **Daily Cap**: 5 rewarded ads per rolling 24 hours per account.
- **Maximum Stored Credits**: Cap of 20 unspent rewarded credits prevents hoarding.
- **SSV Webhook**: Server stores verification records keyed by `transaction_id` for auditing and duplicate callback suppression.
