# Zynpath Account Deletion Policy Compliance Guide

**Status:** Authoritative  
**Domain:** Google Play Account Deletion Policy (App & Web Requirements)  
**Effective Policy Date:** May 31, 2024 (Updated 2026)  

---

## 1. Google Play Policy Requirements

Google Play's User Data policy stipulates that any app allowing users to create an account must:
1. **In-App Deletion:** Provide an easily discoverable, self-service option within the app to initiate account deletion.
2. **Web-Based Deletion (Without Reinstallation):** Provide a functional web resource link where users can request deletion of their account and associated data without needing to reinstall the mobile app.
3. **Data Scope Disclosure:** Clearly inform users what data will be deleted and what data is retained for legitimate regulatory or legal reasons.
4. **Subscription Notice:** Explicitly warn users that deleting their app account does not automatically cancel Google Play subscriptions.

---

## 2. In-App Deletion Implementation

- **Location:** In the mobile client, navigate to **Settings** → **Account & Cloud Sync** → scroll to **Data Management** → tap **"Delete Account"**.
- **User Interface:** High-contrast `AlertDialog` in `SettingsScreen.kt:475-510` requiring explicit confirmation.
- **Mandatory Subscription Warning Displayed:**
  > *"IMPORTANT NOTICE ON SUBSCRIPTIONS: Deleting your Zynpath account does NOT automatically cancel recurring subscriptions managed through Google Play. You must cancel any active subscription directly in the Google Play Store to prevent future renewal charges."*
- **Execution Flow:**
  1. Client sends authenticated `DELETE /api/v1/account/delete`.
  2. Backend derives the authenticated player ID strictly from `SecurityContext.getCurrentPlayerId()`. Arbitrary client-provided IDs are ignored.
  3. `AccountManagementService.deleteAccount(playerId)` executes an atomic purge across all backend databases.
  4. Local credentials (`zyn_secure_session.enc`) are destroyed.
  5. Pending room synchronization operations are cleared via `SyncOperationDao.clearPendingOperationsForOwner(playerId)`.
  6. Application state returns to Guest mode.

---

## 3. External Web Deletion Implementation (No Reinstallation Needed)

- **Source Template:** `assets/compliance/account_deletion_request.html`
- **Target Deployed URL:** `https://zynpath.com/delete-account` *(Configured in Google Play Console "App Content" section)*
- **Supported Verification Pathways:**
  1. **Direct OAuth Authorization:** Users sign in with their linked Google account. Upon token verification against `https://oauth2.googleapis.com/tokeninfo`, the backend triggers `/api/v1/account/delete`.
  2. **Identity-Verified Request Ticket:** Users input their Public Zynpath ID or registered email. The backend dispatches a cryptographic one-time confirmation link to the verified email address. Deletion executes only when the link is clicked within 24 hours.
- **Protection Against Malicious Deletions:** One user cannot delete another player's account by simply guessing their Public Zynpath ID; cryptographic verification of ownership is mandatory.

---

## 4. Comprehensive Data Purge & Retention Scope

### Data Permanently Deleted Upon Request:
- Cloud account credentials and linked Google/Facebook subject IDs.
- Public Zynpath ID and chosen display name.
- Cloud-saved level progression, stars, and moves history.
- Social graph (friendships, pending friend requests, player blocks).
- Notification queues and push notification tokens.
- Privacy preferences and export logs.
- Active authentication sessions and real-time WebSocket connection tickets.

### Legitimate Retention Exceptions:
1. **Completed Competitive Match Records:**
   - Completed match results in `match_history` are retained to preserve opponent match histories, win/loss stats, and leaderboard rankings.
   - The deleted participant's identity is sanitized and anonymized to display as `[Deleted Player]`.
2. **Financial & Tax Audit Logs:**
   - SHA-256 hashes of Google Play purchase tokens are retained in cold audit tables strictly to prevent duplicate fraudulent entitlement claims and comply with statutory accounting and tax retention regulations (e.g. 7-year statutory retention).

---

## 5. Prevention of Stale Synchronization Resurrections

A critical compliance risk in mobile cloud applications is a stale offline sync worker inadvertently recreating a deleted user record. Zynpath prevents this through three architectural guarantees:
1. **Local Queue Purge:** Account deletion triggers `SyncOperationDao.clearPendingOperationsForOwner(playerId)` which permanently deletes all queued mutations.
2. **SyncCoordinator Account Decoupling:** `SyncCoordinator.onAccountSwitched(null)` halts all in-flight WorkManager jobs.
3. **Backend Reject Policy:** Any delayed HTTP request presenting a deleted player's revoked session token is rejected with HTTP `401 Unauthorized` / `PLAYER_NOT_FOUND`.
