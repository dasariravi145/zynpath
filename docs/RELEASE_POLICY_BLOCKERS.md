# Zynpath Google Play Release Policy Blockers & Pre-Flight Tracking

**Status:** Authoritative  
**Domain:** External Release Blockers & Operational Prerequisites  
**Audit Date:** September 27, 2026  

---

## 1. Prioritized Release Blockers Matrix

The following items are **BLOCKERS** for Google Play production publication. They represent external developer actions, hosting provisions, or Play Console configurations that cannot be resolved solely within the local git repository.

| ID | Blocker Item | Severity | Category | Current Status | Required Resolution Action | Responsible Owner |
|---|---|---|---|---|---|---|
| **BLK-01** | **Public Privacy Policy Web Hosting** | **CRITICAL** | Legal / Policy | `BLOCKED BY CONFIGURATION` | Deploy the drafted privacy policy text (`docs/PRIVACY_POLICY_DRAFT.md`) to a publicly accessible HTTPS URL: `https://zynpath.com/privacy`. | Developer / Web Ops |
| **BLK-02** | **External Account Deletion Portal Web Hosting** | **CRITICAL** | User Data Policy | `BLOCKED BY CONFIGURATION` | Deploy `assets/compliance/account_deletion_request.html` to `https://zynpath.com/delete-account` and connect to backend deletion API. | Developer / Web Ops |
| **BLK-03** | **Official Developer Identity & Support Email** | **HIGH** | Store Presence | `REQUIRES DEVELOPER INFORMATION` | Provide verified developer/publisher organization name, support email (`support@zynpath.com`), and physical/postal address in Play Console. | Developer / Management |
| **BLK-04** | **Google Play Console Subscription Products Configuration** | **CRITICAL** | Monetization | `REQUIRES MANUAL CONSOLE ACTION` | Create and activate in-app subscription base plans (`zynpath_premium_monthly`, `zynpath_premium_6months`) matching catalog IDs. | Play Console Admin |
| **BLK-05** | **Production AdMob App ID & Unit ID Provisioning** | **HIGH** | Advertising | `REQUIRES PRODUCTION CONFIGURATION` | Create production AdMob account, generate real rewarded ad unit ID, and configure in `app/build.gradle.kts` release build type. | AdMob Account Admin |
| **BLK-06** | **Production Backend Hosting & SSL Certificates** | **CRITICAL** | Infrastructure | `REQUIRES PRODUCTION CONFIGURATION` | Deploy Spring Boot backend to production cluster with valid TLS 1.3 certificates on `api.zynpath.com` (Scheduled for Prompt 44). | Backend / DevOps |
| **BLK-07** | **Reviewer Test Account & License Testing Setup** | **MEDIUM** | Review Access | `REQUIRES MANUAL CONSOLE ACTION` | Whitelist Google Play app review test accounts under Play Console **License Testing** to enable sandbox subscription verification. | Play Console Admin |

---

## 2. In-Repository Pre-Requisites Completed (Unblocked)
The following compliance requirements have been **FULLY RESOLVED** at the repository level in Prompt 43:
- [x] Manifest audited; all 6 permissions strictly justified.
- [x] Target SDK set to 36 (Android 16) conforming to 2026 requirements.
- [x] Android Auto Backup rules configured (`data_extraction_rules.xml`) to protect cryptographic session secrets.
- [x] In-app Privacy Policy accessible from Settings screen.
- [x] In-app self-service account deletion implemented and verified.
- [x] External deletion webpage template created (`assets/compliance/account_deletion_request.html`).
- [x] Network security config enforces TLS 1.3 with cleartext disabled in production.
- [x] Data safety questionnaire evidence mapped in full detail.
- [x] Content rating and target audience (13+) decisions fully documented.
