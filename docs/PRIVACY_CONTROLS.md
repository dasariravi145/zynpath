# Zynpath Privacy Controls & Social Discovery Policy

## 1. Overview
Zynpath enforces strict server-side and client-side privacy controls. Players have granular authority over how they are discovered, who can contact them, and what profile details are visible to other players.

---

## 2. Profile Visibility Tiers

The authoritative visibility tier is stored in `player_privacy_settings.profile_visibility`:

- **`PUBLIC`**:
  - Profile is publicly visible in global searches, leaderboards, and duel lobbies.
  - Public fields: Display Name, Public Zynpath ID, Avatar URL, Equipped Avatar Frame, Competitive Rank & Public Match Win Stats.
- **`FRIENDS_ONLY`**:
  - Profile details are masked to non-friends.
  - Appears in mutual friends' friend lists. Non-friends searching the exact ID only see basic display name and public ID without personal statistics.
- **`PRIVATE`**:
  - Profile is hidden from discovery, leaderboards, and general lookups.
  - Existing mutual friendships remain intact, but player cannot be found by new users.

---

## 3. Social Discovery & Direct Lookups

1. **Allow Zynpath ID Search (`allow_zynpath_id_search`)**:
   - When enabled (default: `true`), players can be looked up using exact-match public Zynpath ID (e.g. `ZYN-849201`).
   - When disabled (`false`), exact-match searches via `/api/v1/social/players/search` return `404 Not Found` unless the searcher is already a mutual friend.
2. **Allow Friend Requests (`allow_friend_requests`)**:
   - When enabled (default: `true`), players can receive friend requests and direct duel invitations.
   - When disabled (`false`), any attempt to send a friend request returns an authoritative `403 Forbidden` (`FRIEND_REQUESTS_DISABLED`).

---

## 4. Block Management & Server-Side Enforcement

- **Block Model**: Implemented in `friend_relationships` with status `BLOCKED` and queried via `GET /api/v1/social/blocks`.
- **Enforcement Boundaries**:
  1. **Friend Requests**: Blocked players cannot send or receive friend requests from the blocker.
  2. **Friend Duels**: Blocked players cannot invite the blocker to Quick Duels or Friend Duels.
  3. **Mini Leagues**: Blocked players cannot invite each other to private rooms.
  4. **Notifications**: Push notifications and in-app alerts from blocked players are dropped server-side.
  5. **Discovery**: Blocked players cannot view the blocker's profile.
- **Unblock Workflow**: Players can unblock users at any time via the dedicated Privacy screen (`DELETE /api/v1/social/blocks/{targetPlayerId}`).

---

## 5. Public vs Private Data Segregation

| Field | Publicly Accessible | Conditions |
|---|---|---|
| Display Name | Yes | Subject to visibility tier |
| Public Zynpath ID | Yes | If `allowZynpathIdSearch` is true |
| Avatar & Avatar Frame | Yes | Subject to visibility tier |
| Competitive Rating / Stats | Conditional | Only if visibility is `PUBLIC` or viewer is a friend |
| Solo Level Progress / Stars | No | Strictly private to account / local device |
| In-App Purchase History / Tokens | No | Strictly private; never exposed to social API |
| Linked Email Address | No | Strictly private; never exposed to social API |
| Push Notification Tokens | No | Strictly private; stored encrypted server-side |

---

## 6. Security Hardening & PII Protection (Prompt 36)

- **Object-Level Profile Access Authorization**: Handled via `ResourceAuthorizationService.verifyProfileReadAccess(viewerId, targetId)`. If the target account is set to `PRIVATE`, non-friends and non-owners receive a sanitized not-found response to avoid leaking account existence.
- **Social Abuse Prevention**: `SocialAbuseGuard` strictly validates friend requests and interactions, rejecting self-requests, requests to blocked users, and requests when the target user has disabled friend requests (`allow_friend_requests = false`).
- **PII Scrubbing in Audit Logs**: `SecurityAuditLogger` ensures no sensitive identifiers, email addresses, raw bearer tokens, or push tokens are emitted to application logs. Public Zynpath IDs and internal UUIDs are used exclusively for correlation.
- **Bounded Pagination Limits**: All player search, friend list, and block list endpoints strictly bound query results (`Math.min(limit, 50)` or `100`), preventing memory exhaustion and mass profile scraping attacks.

---

## 7. Achievement Privacy & Telemetry Safeguards (Prompt 41)
- **Local Progress Privacy**: Solo level solutions, touch coordinates, and intermediate drawing gestures are 100% private and never transmitted over the network or logged in telemetry.
- **Account Isolation**: Guest achievements remain strictly stored in local Room DB on device. Upon account linking or multi-account sign-in, achievements and stats are cleanly partitioned by player ID to prevent cross-account data leakage.
- **Voluntary Sharing**: Voluntary social sharing of achievements only shares formatted milestone cards (title and badge) without revealing private account details, email addresses, or unconsented metrics.

---

## 8. In-App Privacy Policy Access & Data Safety Alignment (Prompt 43)
- **Offline In-App Transparency**: An accessible modal dialog viewer in `SettingsScreen.kt` allows players to review Zynpath's complete Privacy Policy and Terms of Service directly on device without requiring an active internet connection.
- **Play Console Data Safety Form Parity**: Every declared field in `docs/DATA_SAFETY_MATRIX.md` matches the actual repository code: zero collection of contacts, location, audio, or free-text messages.
- **Zero Sale of User Data**: Zynpath does not sell, rent, or trade player information or device identifiers to third-party data brokers.
