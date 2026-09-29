# Zynpath Cosmetic Entitlements & Policy Enforcement

## 1. Authoritative Entitlement Gating

Cosmetics rely strictly on the authoritative subscription entitlement architecture established in Prompt 26 (`SubscriptionEntitlementRepository` and `FeatureAccessPolicy`).

### 1.1 Entitlement Keys
- `PREMIUM_THEMES`: Gates premium visual themes (Cyber Neon, Emerald Forest, Solar Amber).
- `PREMIUM_PATH_EFFECTS`: Gates premium path effects (Cyan Energy Pulse, Golden Shimmer, Ember Trail).
- `PREMIUM_AVATAR_FRAMES`: Gates premium avatar frames (Royal Gold Crown, Cyber Ring, Emerald Facet).

No standalone local boolean flags are permitted to authoritatively unlock premium cosmetics.

---

## 2. Server-Authoritative Equipment Validation

Per Prompt 28 Section 32:
- When a client sends a sync request to `POST /api/v1/cosmetics/equipped`, the backend verifies:
  1. That the cosmetic identifiers exist in the catalog.
  2. That any item requiring premium features matches an active, verified subscription record for `playerId` (`SubscriptionService.getEntitlementForAccount(playerId).isEntitled()`).
  3. If unentitled, the request is rejected with HTTP 403 `ENTITLEMENT_REQUIRED`, preventing modified clients from publicly claiming unauthorized cosmetics.

---

## 3. Subscription Lifecycle Behaviors

| Event | Client Behavior | Server Behavior |
|---|---|---|
| **Active Subscription** | Premium cosmetics fully accessible and equippable. | Validates equipment updates. |
| **Subscription Expired** | Saved preferences in DataStore are preserved. Rendering pipeline safely falls back to free defaults (`theme_classic_midnight`, `path_solid_glow`, `frame_default_slate`). | Rejects new premium equipment. Resolves public profile with safe free defaults. |
| **Resubscription** | Authoritative entitlement returns to `ACTIVE`. Stored preferences are instantly re-authorized and restored without manual reselection. | Permits premium equipment. Re-enables public cosmetic display. |
| **Account Switched** | `onAccountSwitched` clears cached entitlement. Reverts to safe defaults unless the new account is independently subscribed. | Binds session to new account ID. Isolates player preferences. |
| **Offline Play** | Bounded cache policy (up to 7 days or `currentPeriodEndMs`) allows temporary offline cosmetic display. Stale flags never grant permanent entitlement. | N/A (Offline). |

---

## 4. Public Profile Security Guarantee

Per Prompt 28 Section 31:
- `PublicPlayerCosmeticsDto` exposes exclusively:
  - `playerId`
  - `themeId`
  - `pathEffectId`
  - `avatarFrameId`
- It is strictly forbidden to transmit Google Play purchase tokens, order IDs, account numbers, or billing metadata in any public multiplayer or friend payload.
