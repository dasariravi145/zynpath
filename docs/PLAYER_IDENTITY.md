# Zynpath Player Identity Architecture

## 1. Overview & Guest-First Philosophy
Zynpath is designed from the ground up as a **guest-first, offline-capable** puzzle platform. A new player opening the application can immediately begin playing Solo puzzles and participating in local Daily Challenges without creating an account, entering an email, or connecting to the internet.

```
+-----------------------------------------------------------------------+
|                         Player Identity                               |
+-----------------------------------+-----------------------------------+
| Internal Local Identity (Private) | Authoritative Identity (Backend)  |
+-----------------------------------+-----------------------------------+
| • Stable random UUID (UUIDv4)     | • Backend-issued Public Zynpath ID|
| • Generated once on first launch  | • Distinct from internal UUID     |
| • Stored in DataStore & Room      | • Safe for friend invitations     |
| • Never derived from hardware     | • Shows 'Unassigned (Guest)' off  |
+-----------------------------------+-----------------------------------+
```

---

## 2. Internal Guest ID vs Public Zynpath ID
To ensure privacy and maintain a clean separation of concerns:

1. **Internal Local Guest ID (`playerId` / `guestUuid`):**
   - Generated once on first app startup using `java.util.UUID.randomUUID().toString()`.
   - Persisted securely in Jetpack DataStore (`PreferencesKeys.GUEST_UUID`) and local Room database (`player_profile.playerId`).
   - **Never derived from:**
     - Device serial number
     - IMEI / MEID
     - Advertising ID (GAID)
     - MAC address or Android hardware identifiers
   - Used internally to anchor local gameplay progress, session snapshots, and personal records.

2. **Public Zynpath ID (`publicZynpathId`):**
   - Implemented in Prompt 18 via backend `PlayerAccountService.generateUniquePublicZynpathId()` and integrated in Prompt 19 for social discovery.
   - Format: `ZYN-<4 Crockford Base32>-<4 Crockford Base32>` (e.g. `ZYN-7749-ECHO`).
   - Globally unique and collision-resistant.
   - Displayed as `Unassigned (Offline Guest)` while playing in guest mode.
   - Safe to share with friends via Android Sharesheet and deep link invitation URLs (`zynpath://invite?id=ZYN-XXXX-YYYY`).
   - Used for exact player discovery (`GET /api/v1/social/players/search?publicId=...`) without exposing email addresses or database keys.

---

## 3. Account Lifecycle & States
The player profile tracks account type using `com.zynpath.game.core.player.AccountType`:
- **`GUEST`:** Default state. Complete local autonomy, offline play, zero registration friction.
- **`LINKING`:** Transient state when connecting to a remote identity provider (Google / Facebook).
- **`LINKED`:** Authenticated state with a verified remote identity record and issued Public Zynpath ID.
- **`LINK_FAILED`:** Error state when network failure or provider conflict occurs during linking; guest progress remains completely intact.

---

## 4. Privacy & Permissions Policy
- **Zero Unnecessary Permissions:** Zynpath does not request camera, contacts, phone state, or location permissions.
- **Hardware Agnostic:** Player identity is portable and fully decoupled from device hardware.
