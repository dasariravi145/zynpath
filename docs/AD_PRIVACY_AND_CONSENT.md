# Zynpath Advertising Privacy and Consent Specification

## 1. Regulatory Framework & User Privacy

Zynpath adheres strictly to data minimization and privacy regulations (GDPR, UK GDPR, CPRA/CCPA, and Google Play Families Policies):
- **Zero PII Transmission:** Zynpath never passes user email addresses, account passwords, purchase tokens, friend lists, or match histories into advertising requests.
- **Audience Declaration:** Zynpath is designed for general audiences aged 13+. Underage or mixed-audience targeting is governed by the `TAG_FOR_CHILD_DIRECTED_TREATMENT_UNSPECIFIED` and `TAG_FOR_UNDER_AGE_OF_CONSENT_UNSPECIFIED` settings until formal Play Console declarations are finalized.
- **Data Minimization:** Advertising requests only supply the minimum parameters required by Google Mobile Ads SDK for ad serving and fraud mitigation.

---

## 2. Consent Management Platform (CMP)

- **Engine:** Google User Messaging Platform (UMP) SDK integration foundation.
- **Workflow:**
  1. Check consent requirements via `AdConsentManager.requestConsentInfoUpdate()`.
  2. If consent is required, present the official UMP consent form (`showConsentFormIfRequired()`).
  3. Ads are loaded **only** after consent requirements are satisfied.
  4. If consent is denied or revoked, personalized ad requests are halted, falling back to non-personalized ad requests or suppressing ads entirely based on policy.

---

## 3. User Privacy Options Entrypoint

In compliance with Google Play requirements, users can revisit their consent choices at any time:
- Located in **Settings Screen → PRIVACY & ADVERTISING → Ad & Privacy Preferences**.
- Tapping this option invokes `AdConsentManager.showPrivacyOptionsForm()`.
- Users retain sovereign control over tracking, personalization, and analytics choices.
