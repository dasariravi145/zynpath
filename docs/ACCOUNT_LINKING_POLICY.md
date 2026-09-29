# Zynpath Account Linking Policy

## 1. Principles
1. **Never Force Authentication**: Free Solo gameplay and core puzzle mechanics remain fully functional for guest accounts.
2. **Deterministic Identity Resolution**: Account linking is validated strictly through provider server-to-server token exchange (Google Play Games token or Facebook access token). Account linking NEVER relies on matching display names or unprotected email addresses.
3. **Collision Handling**:
   - If a provider identity is already linked to another Zynpath account, the linking request fails with `PROVIDER_ALREADY_LINKED` or `PROVIDER_OWNERSHIP_CONFLICT`.
   - The user is notified that the provider is already linked to an existing account, and is offered the option to sign in to that account instead of silently overwriting or merging unrelated accounts.
4. **Progress Continuity**:
   - When a guest links to an account for the first time, local level progression, achievements, daily challenge completion history, and equipped cosmetics are automatically merged into the authoritative server profile.
5. **No Last Provider Stranding**:
   - An account must retain at least one valid authentication provider. Unlinking the final provider is strictly rejected server-side.

## 2. Pending Operation Migration (Prompt 35)
- **Atomic Rebinding**: Any un-synchronized guest operations residing in the Room `sync_operations` table are atomically updated to the new authenticated player ID via `SyncOperationDao.rebindOperationsToNewOwner(oldOwner = guestUuid, newOwner = accountId)`.
- **Immediate Coordination**: The `SyncCoordinator` is signaled (`onAccountSwitched`) to schedule an immediate background or foreground sync using the new authenticated credentials.

## 3. Merge Policy & Idempotency
- **Union of Completions**: A level completed either locally as guest or remotely on the account is marked completed (`isCompleted = true`).
- **Personal Best Minimums**: The lowest valid elapsed time in milliseconds is preserved:
  ```
  mergedBestTimeMs = min(localBestTimeMs, remoteBestTimeMs)
  ```
- **Idempotent Application**: Re-linking or submitting duplicate sync batches will never duplicate completions, stars, or achievement increments.

