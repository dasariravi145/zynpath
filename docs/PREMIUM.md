# Zynpath Premium — Subscription Architecture & Monetization Policy

**Application:** Zynpath: Number Path Puzzle  
**Phase:** 6 — Monetization and Premium  
**Status:** Implemented (Prompt 26/50)

---

## 1. Product Philosophy & Core Value

Zynpath is a pure logic puzzle game. Monetization is designed around **convenience and cosmetic customization**, strictly adhering to **zero pay-to-win** principles:

- **Free Core Game:** 100% of core puzzle mechanics, world progression, Daily Challenges, Quick Duels, Friend Duels, Mini Leagues, and standard visual themes remain completely free and accessible without payment.
- **Fair Play Guarantee:** Premium membership never provides gameplay advantages in competitive modes. Hints, extra time, or ranking advantages are strictly prohibited in multiplayer and competitive daily modes.
- **Ad-Free Zen:** Subscribers enjoy an uninterrupted logic experience free of banner, interstitial, or non-rewarded advertising.

---

## 2. Subscription Products & Plans

| Plan Type | Product ID | Base Plan ID | Planned Indicative Price (INR) | Billing Period | Actual Store Price Source |
|---|---|---|---|---|---|
| **Monthly Pass** | `zynpath_premium` | `premium-monthly` | ₹99 | 1 Month (Auto-renewing) | Google Play `ProductDetails` |
| **6-Month Pass** | `zynpath_premium` | `premium-six-months` | ₹499 | 6 Months (Auto-renewing) | Google Play `ProductDetails` |

> [!IMPORTANT]
> The displayed amounts (₹99 and ₹499) are commercial targets. The application **never hardcodes checkout amounts**. All checkout transactions, currency codes, formatted prices, and localized billing terms are dynamically queried from Google Play Billing `ProductDetails`.

---

## 3. Premium Benefits Breakdown

| Benefit Key | Description | Free Allowance | Premium Tier |
|---|---|---|---|
| `AD_FREE` | Suppresses banner and interstitial ads | Ads displayed (optional rewarded ads) | 100% Ad-Free |
| `UNLIMITED_SOLO_HINTS` | Solver-backed step hints in Solo mode | Daily quota (3 free hints) | **Unlimited** |
| `PREMIUM_SOLO_PACKS` | Access to exclusive thematic Solo puzzle collections | Standard Worlds (1–10) | Standard + Bonus Master Packs |
| `PREMIUM_THEMES` | Visual color palettes (Midnight, Neon, Forest Glass) | Default Slate & Classic | All Standard & Premium Themes |
| `PREMIUM_PATH_EFFECTS` | Path line trail shaders (Cyan Pulse, Gold Trail, Ember Glow) | Standard solid path | Full particle trail selector |
| `PREMIUM_AVATAR_FRAMES` | Profile border cosmetics | Standard badge | Exclusive Gold & Animated Borders |
| `ADVANCED_PERSONAL_STATS`| Deep analytics (solve speeds, coverage metrics, difficulty curves) | Standard solve counts & streaks | Full telemetry breakdown |

---

## 4. Competitive Fairness Safeguards

Under [FeatureAccessPolicy](file:///d:/Zynpath/android/app/src/main/java/com/zynpath/game/core/premium/FeatureAccessPolicy.kt), competitive hint access is enforced at the policy and gameplay layer, not merely in the UI:

```kotlin
fun canConsumeHint(gameMode: GameMode, entitlement: PremiumEntitlement, freeHintsRemaining: Int): Boolean {
    // Hard competitive gating: competitive modes reject hints unconditionally
    if (!gameMode.allowsHints) return false
    if (entitlement.isPremiumActive) return true
    return freeHintsRemaining > 0
}
```

Modes where hints are **strictly prohibited for all players**:
1. `QUICK_DUEL` (Multiplayer)
2. `FRIEND_DUEL` (Multiplayer)
3. `MINI_LEAGUE` (Multiplayer)
4. `DAILY_CHALLENGE` (Competitive daily puzzle)

---

## 5. Account Ownership & Guest Policy

- **Guest Account Policy:** Free solo gameplay and daily challenges operate fully without an account. However, initiating a subscription purchase requires an authenticated/linked account (Google or Facebook) to permanently bind the subscription entitlement and prevent lost purchases upon device wiping.
- **Account Isolation:** Entitlements are bound to the internal Zynpath Account ID. When a user signs out or switches accounts, entitlement state is isolated and cleared to prevent leakage.
- **Ownership Conflict Guard:** A purchase token can only be claimed by one account at a time. Re-verification by a foreign account is rejected with HTTP 409 `OWNERSHIP_CONFLICT`.

---

## 6. Lifecycle Management

- **Grace Period (`IN_GRACE_PERIOD`):** Users retain premium access during Google Play payment retry grace periods.
- **Account Hold (`ON_HOLD`):** Access is suspended until payment issues are resolved in the Play Store. A direct deep link to Google Play Subscriptions is provided.
- **Cancellation (`CANCELLED_BUT_ACTIVE`):** When auto-renew is cancelled, entitlement remains active until `currentPeriodEndMs`.
- **Expiration (`EXPIRED`):** Seamlessly returns player to the free tier without loss of progress or puzzle history.
- **Restore Purchases:** Re-queries active Play Store purchases and re-verifies them with the backend.
- **Official Subscription Management:** Deep-links directly to Google Play's native subscription management screen (`https://play.google.com/store/account/subscriptions`).

---

## 7. Premium Solo Puzzle Packs Integration (Prompt 27)

- **Curated Content Access**: Premium subscribers unlock access to curated Solo puzzle packs (`PREMIUM_SOLO_PACKS`) such as *Serpentine Mastery* and *Labyrinth Walls*.
- **Free Content Protection**: All 6 campaign worlds (Levels 1–300) remain 100% free. Premium packs are bonus Solo challenges and are never required to complete the main game.
- **Offline Playability**: Downloaded packs remain playable offline for up to 30 days based on the bounded entitlement cache.
- **Expiration Behavior**: If a subscription expires, active pack gameplay is locked until resubscribed, but installed pack files, completed level records, and personal best times are permanently preserved.

---

## 8. Premium Cosmetics Integration (Prompt 28)

- **Cosmetic Feature Keys**: Enforces `PREMIUM_THEMES`, `PREMIUM_PATH_EFFECTS`, and `PREMIUM_AVATAR_FRAMES` across client and server.
- **Preview Without Purchase**: Players can preview any premium cosmetic in real-time in the Customization screen (`Screen.Cosmetics`) without having an active subscription.
- **Entitlement Checks on Equipment**: The equip action is strictly gated. Free items equip immediately; premium items require an active or bounded-cached entitlement.
- **Subscription Expiration Fallback**: When a subscription lapses, user preferences in DataStore are preserved. However, the runtime active appearance gracefully falls back to free defaults (`theme_classic_midnight`, `path_solid_glow`, `frame_default_slate`).
- **Resubscription Auto-Restoration**: When a player resubscribes, their previously equipped premium items are automatically validated and restored without manual reselection.

---

## 9. Ad-Free Policy & Rewarded Ad Suppression (Prompt 29)

- **Total Ad Suppression (`AD_FREE`)**: Active Premium subscribers have all ad requests, SDK preloads, ad presentation, and reward entrypoints completely suppressed.
- **Unlimited Solo Hints (`UNLIMITED_SOLO_HINTS`)**: Premium subscribers never need to watch rewarded ads to earn hints; their hint counter indicates unlimited availability.
- **Credit Balance Preservation**: Any bonus hint credits earned by the player prior to subscribing are safely preserved in their account wallet and restored if the subscription ever expires.
- **No Degradation Upon Expiration**: If a subscription expires, the player smoothly transitions back to the standard free hint allowance plus their preserved earned bonus credits, with optional rewarded ads re-enabled.

---

## 10. Advanced Personal Statistics & Progression Insights (Prompt 30)

- **Entitlement Feature Key (`ADVANCED_PERSONAL_STATS`)**: Unlocks the dedicated "Premium Insights" tab in the Personal Statistics dashboard (`Screen.Statistics`).
- **Deep Progression Analytics**: Unlocks daily completion velocity charts, first-time vs replay breakdown, same-puzzle time improvements, and per-world speed analytics.
- **Free Statistics Protection**: Free players retain full access to essential progress indicators (completed Solo levels, worlds cleared, daily streaks, basic match history).
- **Zero Fabrication Principle**: Metrics are computed exclusively from verified gameplay records. No numbers or progress are simulated in free previews.
- **Expiration Record Preservation**: When a subscription lapses, detailed historical gameplay records remain stored in Room persistence. Resubscribing instantly restores deep analytics without replaying levels.

---

## 11. Subscription Management & Account Deletion Policy (Prompt 32)

- **Official Google Play Management Path**: Settings includes a direct "Manage Subscription" action routing to the official Play Store subscription center (`SubscriptionConstants.getManageSubscriptionUrl()`).
- **Account Deletion Safeguard**: The account deletion dialog explicitly informs players that deleting their Zynpath account **does NOT cancel recurring Google Play subscriptions**. Subscriptions are governed by Google Play Store policies and must be canceled directly in the Play Store.
- **Restore Purchases**: A dedicated "Restore Purchases" entry allows players to refresh their entitlement status directly from Google Play.

---

## 12. Offline Entitlement Reconciliation & Synchronization Policy (Prompt 35)

- **Strict Server/Store Authority**: Premium entitlement state is authoritative on Google Play Billing and backend verification services. Client sync operations cannot synthesize, lengthen, or modify subscription entitlements.
- **Bounded Offline Cache**: Previously downloaded Premium Solo puzzle packs remain accessible offline up to the bounded 30-day cache period (`offlineEntitlementExpiresAt`).
- **Progress Preservation on Expiration**: When an entitlement expires offline or online:
  - Installed puzzle pack files remain intact on disk.
  - Completed pack levels, star counts, and personal best solve times in Room are preserved.
  - Gated level start actions gracefully prompt the player with a resubscribe dialog instead of purging progress.
- **No False Entitlement Grant**: Network sync failures or offline status never elevate a free tier account to Premium.


