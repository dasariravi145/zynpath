# Zynpath: Number Path Puzzle — Known Limitations & Operational Boundaries

**Document ID:** `DOC-LIMITATIONS-001`  
**Phase:** Phase 12 — Final Release and Project Handoff (Prompt 50/50)  
**Date:** September 2026  
**Status:** Authoritative Limitations Register  

---

## 1. Overview & Transparency Commitment

In accordance with our engineering policy on honest, evidence-based reporting, this document details all known technical limitations, architectural trade-offs, and operational boundaries of **Zynpath: Number Path Puzzle** in its v1.0.0 release candidate state.

---

## 2. Technical & Architectural Limitations

| Limitation ID | Subsystem | Description & Impact | Workaround / Planned Future Enhancement |
|---|---|---|---|
| **LIM-01** | **Host Test Environment** | Automated UI test execution during release validation was conducted on headless Robolectric/Compose harnesses. Direct physical device USB and accelerated AVD emulator runs were blocked by the headless CI host. | Manual device verification must be performed by testers on the Google Play Internal Testing track. |
| **LIM-02** | **Single-Instance Multiplayer** | Spring Boot real-time multiplayer state and matchmaking queues are currently managed in-memory within a single application instance (sufficient for up to ~2,000 concurrent duelists). | Horizontal auto-scaling across multiple container nodes will require introducing a shared Redis pub/sub and distributed lock layer in Phase 13. |
| **LIM-03** | **Play Billing Google Dependency**| In-app billing relies strictly on the official Google Play Billing Library (v7.1.1). Devices lacking Google Play Services (e.g., AOSP forks, Amazon Fire, Huawei AppGallery) cannot complete purchases. | Core Solo gameplay remains 100% accessible to non-Play devices as a free offline experience. |
| **LIM-04** | **Unlinked Guest Device Loss** | Unlinked guest accounts store progress strictly within the local Room database. If a user uninstalls the app or permanently loses their physical device without having linked a Google or Facebook account, cloud progress recovery is impossible. | An explicit in-app warning is presented in Settings prompting users to link their account to protect progress. |
| **LIM-05** | **External Compliance Hosting** | The Privacy Policy and Account Deletion portal templates exist as production-ready HTML files (`assets/compliance/`) but are not yet hosted on a public domain. | Public deployment to `https://zynpath.app/privacy` and `https://zynpath.app/delete-account` must occur prior to production submission. |
| **LIM-06** | **Live In-App Pricing Display** | In-app subscription cards display localized prices formatted directly by Google Play Billing SDK (`ProductDetails.subscriptionOfferDetails`). In local development or unconfigured sandbox environments, pricing falls back to configured fallback strings (₹99 / ₹499). | Live localized currencies and taxes are populated automatically once products are published in Play Console. |
| **LIM-07** | **Push Notification Fallback** | Firebase Cloud Messaging (FCM) push notification delivery requires an active network and Google Play Services. Devices in low-power Doze mode or without Play Services will receive match alerts upon next app launch via the in-app notification center. | Local exact alarms (`AlarmManager`) ensure Daily Challenge reminders trigger reliably on-device regardless of network state. |
