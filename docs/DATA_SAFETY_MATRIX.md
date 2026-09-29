# Zynpath Google Play Data Safety Evidence Matrix

**Status:** Authoritative  
**Domain:** Google Play Console Data Safety Questionnaire Preparation  
**Reference Policy:** Google Play User Data Policy  

---

## 1. Top-Level Data Safety Declarations

| Question | Play Console Declaration | Evidence / Implementation in Codebase |
|---|---|---|
| **Does your app collect or share any of the required user data types?** | **Yes** | App collects limited user data when cloud account is linked, purchases are made, or optional rewarded ads are viewed. |
| **Is all of the user data collected by your app encrypted in transit?** | **Yes** | Enforced by `res/xml/network_security_config.xml` (`cleartextTrafficPermitted="false"`), TLS 1.3 on all REST endpoints (`https://`) and WebSockets (`wss://`). |
| **Do you provide a way for users to request that their data be deleted?** | **Yes** | In-app deletion via **Settings** → **Delete Account** (`AccountController.java`), plus external web deletion portal (`assets/compliance/account_deletion_request.html`). |
| **Does your app allow users to create an account?** | **Yes** | Optional account creation via Google or Facebook OAuth linking (`PlayerAccountService.java`). Core gameplay remains available as Guest without account creation. |
| **Do you commit to follow the Play Families Policy?** | **No** *(Target audience 13+)* | Target audience is explicitly defined as Ages 13+ (General Audience). Families Policy does not apply. |

---

## 2. Detailed Data Type Mapping & Questionnaire Answers

### 2.1 Personal Info

| Data Type | Collected or Shared? | Collection Purpose | Optional or Required? | Encrypted in Transit? | Delete Request Available? | Implementation Details |
|---|---|---|---|---|---|---|
| **User IDs** | **Collected** | • App functionality<br>• Account management | **Optional** (Only collected if player links Google or Facebook account) | **Yes** (TLS) | **Yes** (Immediate purge on account deletion) | Public Zynpath ID (`ZYN-XXXXX`), cloud player UUID, and linked OAuth subject ID (`PlayerAccount.java`). |
| **Name / Display Name** | **Collected** | • App functionality<br>• Personalization | **Optional** (Player chooses display name or defaults to Guest) | **Yes** (TLS) | **Yes** | Self-selected display name used in multiplayer match boards and friend lists. |
| **Email Address** | **Not Collected** | N/A | N/A | N/A | N/A | Email is verified by identity provider (Google/Facebook); email addresses are never stored in Zynpath application databases. |
| **Phone Number / Address** | **Not Collected** | N/A | N/A | N/A | N/A | Zero collection. |

### 2.2 Financial Info

| Data Type | Collected or Shared? | Collection Purpose | Optional or Required? | Encrypted in Transit? | Delete Request Available? | Implementation Details |
|---|---|---|---|---|---|---|
| **Purchase History** | **Collected** | • App functionality<br>• Fraud prevention & legal compliance | **Optional** (Only if purchasing optional Premium subscription or pack) | **Yes** (TLS) | **No** (Retained for statutory tax & anti-fraud audit) | Cryptographic SHA-256 hash of Google Play purchase token and entitlement status (`SubscriptionService.java`). Credit card numbers are handled exclusively by Google Play. |
| **Credit Card / Bank info** | **Not Collected** | N/A | N/A | N/A | N/A | Processed entirely within Google Play Billing system. |

### 2.3 App Info and Performance

| Data Type | Collected or Shared? | Collection Purpose | Optional or Required? | Encrypted in Transit? | Delete Request Available? | Implementation Details |
|---|---|---|---|---|---|---|
| **Crash Logs / Diagnostics** | **Collected** | • Analytics<br>• Developer communications | **Required** (Automatic telemetry) | **Yes** (TLS) | **Yes** (Ephemeral logs auto-expire) | Anonymized error classification (`ErrorClassifier.kt`), OS build number, and hardware model for troubleshooting. |

### 2.4 Device or Other Identifiers

| Data Type | Collected or Shared? | Collection Purpose | Optional or Required? | Encrypted in Transit? | Delete Request Available? | Implementation Details |
|---|---|---|---|---|---|---|
| **Device or other IDs** | **Collected & Shared** | • Advertising (Google Mobile Ads)<br>• Fraud prevention | **Optional** (Only processed if user watches optional rewarded ad or links account) | **Yes** (TLS) | **Yes** | Google Advertising ID handled by Google Mobile Ads SDK for optional rewarded hints; local Guest UUID generated in-app. |

### 2.5 Data Types NOT Collected or Shared
The following Google Play data categories are **NEVER** collected, accessed, or shared by Zynpath:
- **Location:** Zero coarse or fine location access.
- **Photos & Videos:** Zero camera or media gallery access.
- **Audio Files:** Zero microphone or sound recording access.
- **Health & Fitness:** Zero health sensor access.
- **Messages:** Zero SMS or free-text chat access (in-game social interaction is strictly restricted to preset emojis/phrases).
- **Contacts:** Zero address book or contacts access.
- **Calendar:** Zero calendar access.
- **Web Browsing:** Zero browser history access.

---

## 3. Local-Only vs. Off-Device Transmitted Data Summary

| Data Element | Storage Location | Transmitted Off-Device? | Classified as "Collected" under Play Policy? |
|---|---|---|---|
| **Solo Campaign Level Stars & Times** | Local SQLite (`Room`) | Only if cloud account is linked | **No** for offline Guest; **Yes** for linked cloud account. |
| **Offline Daily Challenge Attempts** | Local SQLite (`Room`) | Only when submitting to daily leaderboard | **No** for offline play; **Yes** upon leaderboard verification. |
| **Sound, Music & Haptic Preferences** | Local `DataStore` | **Never** | **No** (Local-only setting). |
| **Hardware Keystore Auth Key** | Local Android KeyStore | **Never** (Excluded from backup via `data_extraction_rules.xml`) | **No** (Local hardware cryptographic key). |
| **In-Memory Drawing Points** | Canvas RAM | **Never** (Only final path coordinate array validated) | **No** (Ephemeral rendering state). |
