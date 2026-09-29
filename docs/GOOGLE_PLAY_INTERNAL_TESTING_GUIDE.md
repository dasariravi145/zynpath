# Google Play Internal Testing Track Guide — Zynpath

**Document ID:** `DOC-PLAY-TEST-001`  
**Application Title:** *Zynpath: Number Path Puzzle*  
**Application ID:** `com.zynpath.game`  
**Target SDK:** 36 (Android 16)  
**Min SDK:** 24 (Android 7.0 Nougat)  
**Status:** Authoritative Release Execution Guide  

---

## 1. Overview of the Internal Testing Track

The **Google Play Internal Testing Track** is the fastest mechanism to distribute real Android App Bundles (`.aab`) to authorized team members, QA testers, and stakeholders. Builds uploaded to this track become available for installation within minutes without undergoing full Google Play policy review.

This guide provides the exact 10-step sequence required to establish and operate the Internal Testing Track for Zynpath.

---

## 2. Step-by-Step Internal Testing Runbook

### Step 1: Google Play Developer Account Access
1. Ensure you have Administrator or Release Manager access to an active Google Play Developer Account.
2. Navigate to [Google Play Console](https://play.google.com/console).
3. If this is a new organization account, verify identity and billing registration status.

### Step 2: Create Application Entry
1. In Google Play Console, click **"Create app"**.
2. Enter the official metadata:
   * **App Name:** `Zynpath: Number Path Puzzle` (28 characters — conforms to 30-char ceiling).
   * **Default Language:** `English (United States) - en-US`.
   * **App or Game:** `Game`.
   * **Free or Paid:** `Free` (monetization is handled via in-app subscriptions and optional ads).
3. Accept the Developer Program Policies and US export laws, then click **"Create app"**.

### Step 3: Google Play App Signing Setup
1. In the left navigation menu, go to **Release** $\to$ **Setup** $\to$ **App integrity**.
2. Under **Play App Signing**, select **"Use Google-generated key"** (recommended for modern Android App Bundles).
3. Google will generate and securely store the authoritative master App Signing Key in Google Cloud HSM.
4. Download the Google Play App Signing public certificate (`deployment_cert.der`) and extract the SHA-1 and SHA-256 fingerprints to register with:
   * Firebase Authentication (for Google Sign-In)
   * Google Cloud Console API credentials
5. The local developer or CI key becomes the **Upload Key**. Every `.aab` submitted to the Console must be signed with this upload key.

### Step 4: Build and Upload Android App Bundle (AAB)
1. Assemble the signed release bundle:
   ```bash
   cd android
   ./gradlew :app:bundleRelease
   ```
   *Generated Artifact:* `android/app/build/outputs/bundle/release/app-release.aab`
2. In Google Play Console, navigate to **Release** $\to$ **Testing** $\to$ **Internal testing**.
3. Click **"Create new release"**.
4. Drag and drop `app-release.aab` into the upload zone.
5. Upload the matching de-obfuscation mapping file:
   * File path: `android/app/build/outputs/mapping/release/mapping.txt`
   * Click **"Upload retrace mapping file"** alongside the uploaded App Bundle.

### Step 5: Tester Configuration (Email List & Opt-In URL)
1. In the Internal testing screen, select the **"Testers"** tab.
2. Under **Email lists**, click **"Create email list"**.
   * List Name: `Zynpath Core QA Team`
   * Add email addresses of verified internal QA testers, architects, and product leads.
3. Save the list and ensure the checkbox next to `Zynpath Core QA Team` is selected.
4. Copy the **"Join on the web"** and **"Join on Android"** opt-in URLs displayed at the bottom of the page.
5. Distribute this URL to testers to authorize installation.

### Step 6: Internal Release Notes
1. Under **Release details**, set:
   * **Release Name:** `1.0.0 (1) - Internal Release Candidate 1`
2. In the **Release notes** text field, paste the verified `<en-US>` notes:
   ```xml
   <en-US>
   Welcome to Zynpath: Number Path Puzzle!
   - 300 handcrafted continuous-path puzzles across Worlds 1 to 6
   - Daily Challenge with global UTC scheduling
   - Real-time Quick Duel 1v1 and Mini League multiplayer
   - Guest-first offline play with full progress persistence
   - Enhanced TalkBack accessibility and responsive tablet support
   </en-US>
   ```

### Step 7: Complete Required Policy Declarations
Even for internal testing, Google Play requires basic declarations:
1. **Target Audience:** Select **"13 and older"** (General Audience; exempt from Families Policy).
2. **Ads Declaration:** Select **"Yes, my app contains ads"** (discloses optional AdMob rewarded hints).
3. **App Access:** Select **"All or some functionality is restricted"** and enter the Reviewer Access credentials documented in `docs/GOOGLE_PLAY_PRELAUNCH_REPORT.md`.

### Step 8: Tester Installation & Device Verification
1. Testers open the opt-in URL on their registered Android devices and accept the internal testing invitation.
2. Testers install Zynpath directly from the private Google Play Store link.
3. Verify:
   * App installs cleanly without package signature conflicts.
   * Splash screen renders smoothly (`Theme.Zynpath.Splash`).
   * Guest Solo play starts immediately with zero network or sign-in prompts.
   * Puzzles respond smoothly to touch drawing and discrete tap mode.

### Step 9: Crash and ANR Monitoring
1. Monitor internal testing crash logs under **Quality** $\to$ **Android vitals** $\to$ **Crashes and ANRs**.
2. Verify that uploaded `mapping.txt` accurately de-obfuscates any runtime exception traces down to exact Kotlin file and line numbers.
3. Verify that zero startup ANRs or fatal unhandled exceptions are reported.

### Step 10: Feedback Collection & Promotion Gate
1. Direct testers to report issues via internal issue tracking or the Play Store internal feedback form.
2. Confirm that all 12 entries in `docs/RELEASE_DEFECT_REGISTER.md` remain verified.
3. Once 100% of internal test scenarios pass with zero regressions over 48 continuous hours, the build is eligible for promotion to **Closed Testing (Alpha)**.

---

## 3. Important Restrictions & Security Notice
* **Never commit upload keystores or passwords to version control.**
* **Never use debug keys for internal testing tracks; Google Play will reject unsigned or debug-signed AABs.**
* **Do not execute live production rollout until internal testing verification is fully signed off by QA leads.**
