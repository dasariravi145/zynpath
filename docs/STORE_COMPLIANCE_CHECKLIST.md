# Google Play Store Compliance & Policy Checklist — Zynpath

## 1. Overview

Before final production submission to Google Play Console, Zynpath must verify compliance with Google Play Developer Program Policies:

---

## 2. Policy Compliance Matrix

| Policy Category | Verification Requirement | Zynpath Implementation | Compliance Status |
| :--- | :--- | :--- | :--- |
| **App Title & Metadata** | Max 30 chars for title; no misleading keywords | `Zynpath: Number Path Puzzle` (28 chars); accurate logic puzzle terms | **COMPLIANT** |
| **Short Description** | Max 80 chars; concise summary of app function | 71 characters; accurately highlights path & checkpoints | **COMPLIANT** |
| **Deceptive Behavior** | Screenshots & text must reflect actual in-app experience | All features (6 worlds, Daily, Duels, Leagues) exist in codebase; no match-3 claims | **COMPLIANT** |
| **Ads Policy** | Disclose ads; rewarded ads must be opt-in | Discloses optional rewarded ads for extra solo hints; active gameplay is ad-free | **COMPLIANT** |
| **In-App Billing** | Google Play Billing must be used for digital goods | Implements Google Play Billing SDK v7.1.1 for Zynpath Premium subscription | **COMPLIANT** |
| **Subscriptions** | Clear billing terms, price, cancellation disclosure | Shows base plan durations, renewal terms, and Settings link to Play Store subscriptions | **COMPLIANT** |
| **User Data & Privacy** | Prominent privacy policy link; accurate Data Safety | In-app modal viewer in `SettingsScreen.kt`; public draft in `docs/PRIVACY_POLICY_DRAFT.md`; Data Safety matrix in `docs/DATA_SAFETY_MATRIX.md` | **COMPLIANT (Hosting Pending)** |
| **Account Deletion** | In-app deletion and web URL without app reinstallation | In-app deletion in Settings; web template in `assets/compliance/account_deletion_request.html` | **COMPLIANT (Hosting Pending)** |
| **Target Audience** | Complete target audience questionnaire | Designed and documented for Ages 13+ (General Audience); no child-directed marketing (`docs/TARGET_AUDIENCE_REVIEW.md`) | **COMPLIANT (Console Pending)** |
| **Families Policy** | Avoid child-directed claims if not participating | Neutral age target; exempt from Families Policy under 13+ classification | **COMPLIANT** |
| **Content Rating** | Complete official IARC questionnaire honestly | Evidence-backed questionnaire mapped in `docs/CONTENT_RATING_PREPARATION.md` (Expected ESRB E / PEGI 3) | **COMPLIANT (Console Pending)** |
| **Permissions** | Minimal, justified permissions | Uses standard network, notifications, and billing; zero location, SMS, camera, or storage | **COMPLIANT** |
| **Android Backup** | Exclude hardware Keystore secrets from backup | `data_extraction_rules.xml` and `backup_rules.xml` exclude `zyn_secure_session.enc` | **COMPLIANT** |

---

## 3. Store Listing Dependencies & Final Phase Roadmap

1. **Prompt 44 (Production Backend Configuration):** Production database schema, migrations, containerization, and environment variables — **COMPLETED**.
2. **Prompt 49 (Release Validation & Security Audit):** Finalize signing configuration, R8 optimization rules, secret scan, dependency audit, defect register, and pre-launch compliance report — **COMPLETED** ([RELEASE_CANDIDATE_REPORT.md](file:///d:/Zynpath/docs/RELEASE_CANDIDATE_REPORT.md), [GOOGLE_PLAY_PRELAUNCH_REPORT.md](file:///d:/Zynpath/docs/GOOGLE_PLAY_PRELAUNCH_REPORT.md)).
3. **Prompt 50 (Google Play Console Final Release Handoff):** Complete the IARC rating questionnaire, publish external privacy and deletion URLs, upload signed bundle to Internal Testing Track, and prepare production launch checklist.

