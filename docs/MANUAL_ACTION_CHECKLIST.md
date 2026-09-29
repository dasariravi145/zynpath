# Zynpath Master Manual Action Checklist & Authorization Registry

**Document ID:** `CHECKLIST-MANUAL-001`  
**Phase:** Phase 12 — Final Release and Project Handoff (Prompt 50/50)  
**Date:** September 2026  
**Status:** Authoritative Operator Authorization Registry  

---

## 1. Overview & Operational Policy

In strict accordance with our engineering and release policies, the automated coding agent **never executes actions that incur financial costs, modify production DNS, create production cryptographic credentials, or publish application releases to Google Play**.

This document explicitly catalogues every action that **requires manual execution or explicit authorization by the human operator / product owner**.

---

## 2. Master Manual Action Registry

| # | Action Name | Category | Exact Manual Step Required from User | Prerequisite Documents | Status |
|---|---|---|---|---|---|
| **01** | **Create Upload Keystore** | Cryptography & Security | Generate Java Keystore (`zynpath-upload-key.jks`) using `keytool`. Store securely outside Git. Base64-encode and save in password vault. | `docs/RELEASE_SIGNING.md` | **REQUIRES USER ACTION** |
| **02** | **Inject CI Secrets** | CI/CD DevOps | In GitHub repository settings $\to$ **Secrets and variables** $\to$ **Actions**, add `ZYNPATH_UPLOAD_KEYSTORE_BASE64`, `ZYNPATH_UPLOAD_KEY_PASSWORD`, and `ZYNPATH_KEY_ALIAS`. | `docs/CI_CD_PIPELINE.md` | **REQUIRES USER ACTION** |
| **03** | **Host Compliance URLs** | Web Infrastructure | Deploy `assets/compliance/privacy_policy.html` and `account_deletion_request.html` to a public web host so they resolve at `https://zynpath.app/privacy` and `https://zynpath.app/delete-account`. | `docs/GOOGLE_PLAY_COMPLIANCE.md` | **REQUIRES USER ACTION** |
| **04** | **Register App in Console** | Google Play Store | Log into Google Play Console, click **"Create app"**, enter Title `Zynpath: Number Path Puzzle`, default language `en-US`, Game / Free. | `docs/GOOGLE_PLAY_INTERNAL_TESTING_GUIDE.md` | **REQUIRES USER ACTION** |
| **05** | **Enroll in Play App Signing** | Google Play Store | Navigate to **Release $\to$ Setup $\to$ App integrity** and enroll in Google Play App Signing using the Google-generated key option. | `docs/GOOGLE_PLAY_APP_SIGNING.md` | **REQUIRES USER ACTION** |
| **06** | **Register OAuth Client IDs** | Identity & Auth | In Google Cloud Console, register Android OAuth Client ID with SHA-1 fingerprints from Google Play App Signing certificate and local debug key. | `docs/THIRD_PARTY_CONFIGURATION.md` | **REQUIRES USER ACTION** |
| **07** | **Create In-App Subscriptions** | Monetization | In Play Console $\to$ **Monetize $\to$ Subscriptions**, create `zynpath_premium_monthly` (₹99) and `zynpath_premium_6months` (₹499). | `docs/GOOGLE_PLAY_BILLING.md` | **REQUIRES USER ACTION** |
| **08** | **Submit IARC Questionnaire** | Store Policy | In Play Console $\to$ **Policy $\to$ App content $\to$ Content ratings**, complete the questionnaire using the exact answers provided in documentation. | `docs/CONTENT_RATING_PREPARATION.md` | **REQUIRES USER ACTION** |
| **09** | **Complete Data Safety Form** | Store Policy | In Play Console $\to$ **Policy $\to$ App content $\to$ Data safety**, complete the questionnaire matching the audited disclosures. | `docs/DATA_SAFETY_MATRIX.md` | **REQUIRES USER ACTION** |
| **10** | **Declare Target Audience** | Store Policy | Select **Ages 13 and older** (General Audience) in Play Console App Content. Confirm non-participation in Designed for Families. | `docs/TARGET_AUDIENCE_REVIEW.md` | **REQUIRES USER ACTION** |
| **11** | **Provision Production Cloud DB**| Cloud Infrastructure | Provision managed PostgreSQL 16+ instance in private VPC subnet. Set connection credentials in `infrastructure/production.env`. | `docs/PRODUCTION_DEPLOYMENT_RUNBOOK.md` | **REQUIRES USER ACTION** |
| **12** | **Configure Production DNS** | Cloud Infrastructure | Point DNS `api.zynpath.app` to edge reverse proxy / load balancer with valid SSL certificate. | `docs/PRODUCTION_DEPLOYMENT_RUNBOOK.md` | **REQUIRES USER ACTION** |
| **13** | **Enroll Internal Testers** | QA Operations | In Play Console Internal Testing track, create email list `Zynpath Core QA Team`, add tester emails, and share opt-in URL. | `docs/GOOGLE_PLAY_INTERNAL_TESTING_GUIDE.md` | **REQUIRES USER ACTION** |
| **14** | **Upload Internal Release AAB**| Release Engineering | Trigger GitHub Actions release workflow or build locally (`./gradlew :app:bundleRelease`) and upload `app-release.aab` and `mapping.txt` to Console. | `docs/RELEASE_ARTIFACT_MANIFEST.md` | **REQUIRES USER ACTION** |
| **15** | **Promote to Staged Rollout** | Release Engineering | Once 100% of internal tests pass, promote release to Production track using 5-day staged rollout (5% $\to$ 10% $\to$ 25% $\to$ 50% $\to$ 100%). | `docs/GOOGLE_PLAY_PRODUCTION_CHECKLIST.md` | **REQUIRES USER ACTION** |

---

## 3. Strict Proscription Notice

The automated assistant will **NEVER**:
* Publish or upload an application bundle automatically.
* Spend money, register domains, or provision cloud servers.
* Alter production DNS records or live Google Play Store listings.
* Generate or commit production signing credentials.

All items listed above must be executed by authorized human personnel using the provided runbooks.
