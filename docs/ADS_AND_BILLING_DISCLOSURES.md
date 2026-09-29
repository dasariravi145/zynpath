# Zynpath Advertising & Billing Compliance Disclosures

**Status:** Authoritative  
**Domain:** Google Play Monetization, Subscriptions, and Advertising Compliance  

---

## 1. Google Play Billing & Subscriptions Compliance

Zynpath utilizes the official **Google Play Billing Library v7.1.1** (`com.android.billingclient:billing-ktx:7.1.1`) for all digital goods and subscription offerings. No alternative third-party payment links or out-of-band payment webviews are present in the mobile application.

### 1.1 Subscription Products
| Product ID | Billing Period | Planned Target Price | Entitlements Conferred | Competitive Gating |
|---|---|---|---|---|
| `zynpath_premium_monthly` | 1 Month (Auto-renewing) | ₹99 / month (or local equivalent) | • Complete ad suppression<br>• All Premium visual themes<br>• Exclusive avatar frames<br>• Access to Premium puzzle packs<br>• Unlimited Solo campaign hints | **Strictly Prohibited:** Zero hints or gameplay advantages in Quick Duel, Friend Duel, or Mini League. |
| `zynpath_premium_6months` | 6 Months (Auto-renewing) | ₹499 / 6 months (or local equivalent) | Same benefits as monthly subscription with bundle value pricing. | **Strictly Prohibited:** Zero competitive advantage. |

*Note on Pricing: The prices listed above represent planned base product pricing. In accordance with Google Play Policy, live pricing is dynamically queried from Google Play Services using `BillingClient.queryProductDetailsAsync` to display exact, localized currency amounts and taxes to players.*

### 1.2 Subscription Presentation & Transparency
In compliance with Google Play's Subscription Policy:
1. **Clear Price & Period:** The subscription screen (`PremiumScreen.kt`) prominently displays the subscription title, periodic price, billing frequency, and automatic renewal terms before the user initiates purchase.
2. **Cancellation Guidance:** In-app screens explicitly explain how to cancel subscriptions directly via Google Play:
   > *"Subscriptions renew automatically unless cancelled at least 24 hours before the end of the current billing cycle. You can manage or cancel your subscription at any time in the Google Play Store under Account > Payments & subscriptions."*
3. **Restore Purchases:** The application includes a prominent **"Restore Purchases"** action in `PremiumScreen.kt`. Players can restore existing entitlements across devices without duplicate charges.
4. **Backend Cryptographic Verification:** Entitlements are never unlocked based solely on client claims. The backend `SubscriptionService.java` verifies the purchase token against Google Play Developer APIs and records the SHA-256 hash.

---

## 2. Advertising Disclosures (Google Mobile Ads)

Zynpath integrates the official **Google Mobile Ads SDK v23.6.0** (`com.google.android.gms:play-services-ads:23.6.0`) with a player-first, non-intrusive design philosophy.

### 2.1 Rewarded Ad Experience
- **100% Optional Engagement:** Zynpath contains **zero forced ads**, zero interstitial splash ads, and zero persistent banner overlays that obstruct the puzzle board.
- **Single Use Case:** Rewarded video ads exist solely for free players who voluntarily choose to watch a video to receive bonus hints for Solo campaign levels.
- **Fair-Play Isolation:** Rewarded hint credits can **never** be used in competitive multiplayer modes (Quick Duel, Friend Duel, Mini League).
- **Fraud-Resistant Server Verification (SSV):** Hint credits are awarded only after AdMob server-side verification (SSV) callback confirms completion. Dismissing or closing an ad prematurely never awards credits.
- **Daily Caps & Wallet Limits:**
  - Maximum of 5 rewarded ads per 24-hour UTC day.
  - Maximum wallet capacity of 10 bonus hint credits.

### 2.2 Complete Ad Suppression for Premium Subscribers
Active Premium subscribers enjoy a complete, system-wide suppression of all advertising SDK calls. The `GameplayViewModel` checks the player's entitlement state; when Premium is active, all rewarded ad entry points and video load requests are completely deactivated.

### 2.3 Google Play Console Ad Declaration
- **Declaration:** In the Google Play Console **App Content → Ads** section, the developer must select **"Yes, my app contains ads"**.
- **Accurate Description:** Disclose that ads are limited to optional rewarded video advertisements via Google Mobile Ads.
