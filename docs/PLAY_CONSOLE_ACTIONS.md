# Zynpath Google Play Console Manual Action Checklist

**Status:** Authoritative  
**Domain:** Google Play Console Configuration & Manual Release Actions  
**Applicability:** Google Play Submission (Prompts 49–50)  

---

## 1. Store Presence Configuration

- [ ] **Main Store Listing Copy:**
  - **App Name (28/30 chars):** `Zynpath: Number Path Puzzle`
  - **Short Description (71/80 chars):** `One path. Every number. Connect checkpoints and cover the whole board!`
  - **Full Description:** Copy from `assets/store/metadata_en_US.json`.
- [ ] **Graphic Assets:**
  - **App Icon:** Upload 512 × 512 px PNG (generated from `assets/branding/logo_mark.svg`).
  - **Feature Graphic:** Upload 1024 × 500 px PNG (exported from `assets/store/feature-graphic/feature_graphic_1024x500.svg`).
  - **Phone Screenshots:** Upload canonical 8-screen sequence outlined in `assets/store/screenshots/screenshot_manifest.json`.
- [ ] **Categorization & Contact Details:**
  - **Category:** Games → Puzzle
  - **Tags:** Puzzle, Brain Teaser, Logic, Casual, Offline
  - **Contact Email:** Enter developer support email (`support@zynpath.com`).
  - **Website:** Enter official website (`https://zynpath.com`).

---

## 2. Policy & App Content Declarations

- [ ] **Privacy Policy:**
  - Enter public hosting URL: `https://zynpath.com/privacy`.
  - Ensure webpage is live and displays text matching `docs/PRIVACY_POLICY_DRAFT.md`.
- [ ] **Ads Declaration:**
  - Select **"Yes, my app contains ads"**.
  - Disclose optional rewarded video ads through Google Mobile Ads.
- [ ] **App Access (Reviewer Instructions):**
  - Select **"All or some functionality is restricted"**.
  - Provide instructions: *"Core Solo puzzle gameplay is 100% accessible immediately in Guest mode without login. For online multiplayer and profile cloud sync, use reviewer test credentials provided in Developer Notes."*
- [ ] **Content Ratings (IARC):**
  - Enter contact email.
  - Complete questionnaire using evidence documented in `docs/CONTENT_RATING_PREPARATION.md`.
  - Expected result: ESRB Everyone, PEGI 3.
- [ ] **Target Audience & Content:**
  - Select target age groups: **13–15**, **16–17**, **18 and over**.
  - Families Policy: Select **No**.
  - Unintentional Appeal: Select **No**.
- [ ] **Data Safety Questionnaire:**
  - Complete all sections using the exact answers in `docs/DATA_SAFETY_MATRIX.md`.
  - Disclose user IDs (optional account), purchase token hashes (financial info), diagnostics, and device IDs (ads).
- [ ] **Account Deletion URLs:**
  - Check the box: *"My app allows users to create an account."*
  - In-App deletion steps: Describe Settings → Data Management → Delete Account.
  - Web deletion URL: Enter `https://zynpath.com/delete-account` (hosted via `assets/compliance/account_deletion_request.html`).

---

## 3. Monetization & Subscriptions Setup

- [ ] **Create In-App Subscriptions:**
  - Product ID 1: `zynpath_premium_monthly`
    - Base plan: 1 Month auto-renewing.
    - Set base price (e.g. ₹99 INR / $1.99 USD).
  - Product ID 2: `zynpath_premium_6months`
    - Base plan: 6 Months auto-renewing.
    - Set base price (e.g. ₹499 INR / $8.99 USD).
- [ ] **Configure License Testing Whitelist:**
  - Add reviewer email addresses and developer test Google accounts under **Setup → License testing** to allow sandbox test purchases without credit card charges.

---

## 4. Release Track Execution

- [ ] **Upload Android App Bundle (AAB):**
  - Target API 36, Release signed with upload key.
- [ ] **Review Pre-Launch Report:**
  - Inspect Firebase Test Lab automated tests for crashes, ANRs, or accessibility warnings across diverse Android devices.
- [ ] **Promote to Closed / Open Testing Track:**
  - Execute closed beta testing prior to public production rollout.
