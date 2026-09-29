# Zynpath Account Deletion Workflow

## 1. Overview
Zynpath provides a self-service **Delete Account** option directly inside the app under Settings → Data & Storage. Deleting an account permanently purges the player's personal records from the server while upholding external billing realities and multiplayer record integrity.

---

## 2. Deletion Process & Lifecycle

1. **User Action**:
   - Player taps **Delete Account**.
   - An explicit confirmation dialog appears.
2. **Explicit Play Store Warning**:
   - The dialog specifically warns:
     > **IMPORTANT NOTICE ON SUBSCRIPTIONS:**
     > Deleting your Zynpath account does NOT automatically cancel recurring subscriptions managed through Google Play. You must cancel any active subscription directly in the Google Play Store to prevent future renewal charges.
3. **Execution**:
   - Client sends authenticated `DELETE /api/v1/account/delete`.
   - Backend executes `AccountManagementService.deleteAccount(playerId)`.
4. **Data Purge**:
   - Active player sessions are revoked.
   - Social relationships (friends, outgoing/incoming requests, blocks) are wiped.
   - In-app notification queues and push registrations are purged.
   - Privacy settings and export request logs are erased.
   - Player account record is marked deleted and removed.
   - Deletion audit record is logged with timestamp for legal compliance.
5. **Local Cleanup**:
   - Secure keystore credentials are destroyed.
   - In-memory state is cleared.
   - Pending synchronization operations in Room `sync_operations` for the deleted player are permanently purged via `SyncOperationDao.clearPendingOperationsForOwner(playerId)`.
   - `SyncCoordinator.onAccountSwitched(null)` halts all sync tasks.
   - Device returns to Guest mode.

---

## 3. Competitive Integrity Preservation
Multiplayer match history for other participants is NOT corrupted when an opponent deletes their account. Past completed matches retain the opponent as `[Deleted Player]` with historical scores intact, preserving the competitive ladder and leaderboard mathematics.

---

## 4. No Stale Resurrections (Prompt 35)
Purging pending sync operations on account deletion prevents stale or delayed client queues from attempting to push updates to a deleted account or resurrecting deleted user records on the server.

---

## 5. Distinction from Local Guest Data Reset
- **Delete Account**: Deletes cloud account records, server progression, social graph, and purges account-bound sync operations.
- **Reset Local Guest Progress**: Clears only the local Room database and DataStore preferences on the current device. It does not affect server accounts.

---

## 6. Security Hardening & Authorization (Prompt 36)

- **Ownership & Access Verification**: Account deletion is strictly guarded by `@RequireAccess(OWNER_ONLY)` and `ResourceAuthorizationService.verifyOwnership`. The backend derives the target account strictly from `SecurityContext.getCurrentPlayerId()`; arbitrary client-supplied account IDs are rejected.
- **Sensitive Rate Limiting**: Protected by `RateLimitPolicy.SENSITIVE` (max 5 requests/hour per account/IP), mitigating malicious automated deletion attempts.
- **Atomic Global Session Revocation**: Deletion instantly revokes all active session tokens via `SessionSecurityService.revokeAllSessionsForPlayer`, halts all connected WebSockets, and purges push tokens.
- **Security Audit Logging**: Every account deletion produces a structured `ACCOUNT_DELETED` event in `SecurityAuditLogger` containing the sanitized public ID and timestamp for legal and compliance audit trails.

---

## 7. Web Deletion Portal & Play Console Compliance (Prompt 43)

- **External Web Portal Template**: Configured in `assets/compliance/account_deletion_request.html` for deployment to `https://zynpath.com/delete-account`.
- **Zero App Reinstallation Prerequisite**: Conforms to Google Play's mandate allowing users to initiate account and data deletion from any desktop or mobile web browser without having the app installed.
- **Ownership Verification**: Supports direct Google OAuth verification or one-time cryptographic confirmation links sent to the player's registered email address.
- **Subscription Guidance**: Both the in-app dialog and external web portal explicitly instruct users to cancel active Google Play subscriptions in `play.google.com/store/account/subscriptions`.

---

## 8. Account Deletion Integration & Verification (Prompt 47)

### 8.1 Test Execution Summary (`AccountControllerIntegrationTest`)
- **Suite**: `com.zynpath.backend.account.AccountControllerIntegrationTest`
- **Total Tests**: 7 / 7 PASSED (100% Pass Rate).
- **Verified Behaviors**:
  1. `deleteAccount_authenticated_shouldSucceed`: Confirms authenticated deletion request permanently removes account, revokes sessions, and returns 200 OK.
  2. `deleteAccount_unauthenticated_shouldReject`: Confirms unauthenticated deletion attempts are rejected with 401 Unauthorized.
  3. `deleteAccount_crossAccount_shouldBeForbidden`: Verified Player A cannot delete Player B's account (target derived strictly from SecurityContext).
  4. `staleSync_afterDeletion_cannotResurrect`: Sync operations submitted for a deleted account are rejected without recreating the user.

### 8.2 Defect Fixed and Reverified
- **Defect**: `AccountController.java` had conflicting stacked `@PostMapping("/delete")` and `@DeleteMapping("/delete")` annotations on identical handler methods, causing Spring AmbiguousHandlerMethodException during startup.
- **Resolution**: Refactored to unified `@RequestMapping(value = "/delete", method = {RequestMethod.POST, RequestMethod.DELETE})`. Startup and deletion endpoints reverified with 100% test pass.
- Detailed report in [`docs/BACKEND_INTEGRATION_TEST_REPORT.md`](file:///d:/Zynpath/docs/BACKEND_INTEGRATION_TEST_REPORT.md).


