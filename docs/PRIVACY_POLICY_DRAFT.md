# Privacy Policy for Zynpath: Number Path Puzzle

**Last Updated:** September 27, 2026  
**Effective Date:** September 27, 2026  
**Application:** Zynpath: Number Path Puzzle  
**Developer / Publisher:** [DEVELOPER_OR_STUDIO_NAME]  
**Privacy Contact:** `privacy@zynpath.com`  

---

## 1. Introduction

Welcome to **Zynpath: Number Path Puzzle** ("Zynpath", "we", "us", or "our"). We are committed to protecting your personal privacy and providing a transparent, enjoyable puzzle experience.

Zynpath is designed on a **Guest-First, Offline-First** architecture. You can play 100% of our Solo campaign puzzle levels and solve daily puzzles completely offline without creating an account or providing any personal details.

This Privacy Policy explains what information we collect, how it is used, how it is secured, and your rights to access or delete your data.

---

## 2. Information We Collect and How We Collect It

### 2.1 Information Stored Locally on Your Device (Offline First)
When you play Zynpath as a Guest or offline, the following data is stored locally on your device in our sandboxed database (`Room` / `DataStore`):
- **Puzzle Progress:** Unlocked levels, completion times, moves count, star ratings, and World completion flags across 300 levels.
- **Daily Challenge:** Local completion history, solution moves, and personal streak count.
- **Player Preferences:** Sound volume, haptic feedback toggle, reduced motion preference, theme choice, and board contrast mode.
*This local data is not transmitted to our servers unless you explicitly choose to link a cloud account.*

### 2.2 Information Collected When You Link an Account (Optional)
If you choose to link a Google or Facebook account to enable cloud backup or online multiplayer, we collect:
- **Authentication Identifiers:** A unique user identifier provided by Google or Facebook to authenticate your account. We never receive or store your third-party account password.
- **Public Profile Data:** Your chosen display name, avatar frame selection, and a randomly assigned Public Zynpath ID (e.g. `ZYN-84920`).
- **Cloud Progression:** Your level stars, achievement unlocks, and daily streaks to synchronize progress across your devices.

### 2.3 Online Multiplayer & Social Features
When you participate in online multiplayer modes (Quick Duel, Friend Duel, Mini League):
- **Matchmaking Records:** Match start and end timestamps, completion duration, and win/loss records.
- **Social Graph:** Your friends list, pending invitations, and blocked player list.
- **Preset Communication:** In-game interactions are strictly limited to preset positive emojis and phrases. We do **not** support or record free-form text or audio chat.

### 2.4 In-App Purchases & Subscriptions
- When you purchase an optional Premium subscription or puzzle pack via **Google Play Billing**, Google Play securely processes your payment.
- We receive and store an encrypted cryptographic hash of the Google Play purchase token and subscription status (e.g., active, expired) to verify your entitlements. We never collect or store your credit card or financial account numbers.

### 2.5 Advertising Data (Optional Rewarded Hints)
- Free players may voluntarily choose to watch a rewarded video ad via **Google Mobile Ads (AdMob)** to receive bonus hints for Solo levels.
- Google Mobile Ads may collect standard device identifiers (such as the Google Advertising ID) and diagnostic telemetry to serve relevant ads in accordance with Google's Advertising Policies.
- Premium subscribers experience an entirely ad-free environment.

### 2.6 Technical Telemetry & Crash Diagnostics
- To maintain application stability, we collect non-identifiable technical diagnostics including device model, operating system version, app build number, and anonymized error classifications.

---

## 3. How We Use Your Information

We process collected information strictly for legitimate application purposes:
1. **To deliver core puzzle gameplay** and maintain your level progress.
2. **To enable online multiplayer matchmaking**, synchronized countdowns, and fair-play validation.
3. **To manage and restore purchases** and subscriptions via Google Play Billing.
4. **To prevent fraud, cheating, and abuse** through server-authoritative move validation.
5. **To provide customer support** and respond to data inquiries.

**We do NOT sell, rent, or trade your personal information to third parties.**

---

## 4. Third-Party Service Providers

We utilize trusted third-party SDKs that process limited data to support app operations:
- **Google Play Services & Google Play Billing:** For app distribution, platform updates, and subscription processing. [Google Privacy Policy](https://policies.google.com/privacy).
- **Google Mobile Ads (AdMob):** For serving optional rewarded video ads to free players. [Google Ads Privacy](https://policies.google.com/technologies/ads).
- **Google / Facebook Identity Providers:** For optional cloud account authentication.

---

## 5. Data Security

We implement rigorous technical safeguards to protect your data:
- **Encryption in Transit:** All network communication between the application and our servers is strictly encrypted using Transport Layer Security (TLS 1.3 / HTTPS and WSS). Cleartext traffic is disabled in production.
- **Encryption at Rest:** Sensitive authentication session tokens on your Android device are encrypted using hardware-backed keys via the **Android KeyStore** (`AES/GCM/NoPadding`).
- **Server Access Control:** Zero-trust architecture with deny-by-default role-based access and sliding-window rate limiting.

---

## 6. Data Retention and Account Deletion

### 6.1 In-App Self-Service Deletion
You can permanently delete your Zynpath account and all associated cloud data at any time directly in the app:
- Open **Settings** → **Data & Storage** → tap **"Delete Account"**.
- Confirm the deletion prompt.

### 6.2 External Web Deletion Portal (No App Reinstallation Required)
In compliance with Google Play Developer Policy, you may also request permanent account deletion via our official web portal without reinstalling the application:
- **Portal URL:** `https://zynpath.com/delete-account` *(Hosted via `assets/compliance/account_deletion_request.html`)*
- You can authenticate via Google Sign-In or submit an identity-verification ticket.

### 6.3 What Happens When You Delete Your Account
- **Immediately Purged:** Your cloud Player ID, public profile, linked OAuth credentials, friends list, pending invitations, notification preferences, and active sessions are permanently erased.
- **Local Progress:** Local progress on your current device can be independently erased via **Settings** → **"Reset Local Guest Progress"**.
- **Retention Exceptions:** Past completed multiplayer match records retain an anonymized participant entry (`[Deleted Player]`) to maintain competitive leaderboard integrity. Anonymized purchase token hashes are retained solely to satisfy legal, tax, and anti-fraud audit obligations.

### 6.4 Notice Regarding Google Play Subscriptions
> **IMPORTANT:** Deleting your Zynpath account does **NOT** automatically cancel active recurring subscriptions managed through Google Play. You must cancel active subscriptions directly in your [Google Play Subscriptions Center](https://play.google.com/store/account/subscriptions) to avoid future renewal charges.

---

## 7. Children's Privacy

Zynpath is a general-audience logic puzzle game designed for users **aged 13 and older** (or the applicable digital age of consent in your jurisdiction). We do not knowingly collect personal information from children under 13. If you believe a child under 13 has provided personal information to us, please contact us at `privacy@zynpath.com` so we can promptly delete it.

---

## 8. International Data Transfers

If you access our online multiplayer services from outside the server hosting region, your information may be transferred and processed in secure cloud facilities where our servers operate. We ensure appropriate cross-border data protection mechanisms are in place.

---

## 9. Changes to This Privacy Policy

We may update this Privacy Policy from time to time to reflect policy, technological, or legal developments. When changes are made, we will update the "Last Updated" date at the top of this policy and provide prominent notice within the app where appropriate.

---

## 10. Contact Us

If you have questions, feedback, or data privacy requests (such as requesting a copy of your personal data or exercising statutory data rights), please contact:

**Privacy Officer**  
Email: `privacy@zynpath.com`  
Support: `support@zynpath.com`  
Website: `https://zynpath.com`  
Postal Address: `[DEVELOPER_POSTAL_ADDRESS_PLACEHOLDER]`  
