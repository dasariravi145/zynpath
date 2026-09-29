# Zynpath Invitation Links & Deep Link Routing

## Overview
Zynpath enables players to discover and invite each other using shareable links and Android deep links, without exposing private credentials or requiring third-party social permissions.

---

## 1. Link Formats
1. **Custom Scheme Deep Link:**
   ```
   zynpath://invite?id=ZYN-XXXX-YYYY
   ```
   Directly handled by the Android application via intent filters.
2. **Web Fallback / App Link:**
   ```
   https://zynpath.com/invite?id=ZYN-XXXX-YYYY
   ```
   Fallback link suitable for messaging apps, social shares, and users who do not have the app installed yet.

---

## 2. Security & Privacy Rules
- **No Private Identifiers:** Invitation links contain only the versioned Public Zynpath ID.
- **Excluded Sensitive Data:** Links must **never** contain access tokens, refresh tokens, internal UUIDs, email addresses, or provider subject IDs.
- **Format Validation:** Incoming deep link parameters are strictly validated using regex `^ZYN-[A-Z0-9]{4,12}(-[A-Z0-9]{4,12})?$`. Malformed or unknown IDs are safely discarded.

---

## 3. Deep Link Resolution Flow
1. User taps invitation link outside Zynpath (e.g., in a messaging app).
2. Android OS launches `MainActivity` with intent filter `ACTION_VIEW`.
3. Navigation Compose intercepts the deep link route `friends?invitationId={invitationId}`.
4. `FriendsScreen` checks authentication status:
   - **If Signed In:** Auto-populates the "Find Player" search bar with the inviter's ID and searches their public profile, presenting an immediate "Send Friend Request" button.
   - **If Guest:** Displays the guest notice and sign-in guidance while preserving the pending invitation ID in state so the connection can proceed seamlessly upon account linking.

---

## 4. Android Sharesheet Integration
`InvitationLinkHelper.createShareIntent` crafts standard `Intent.ACTION_SEND` intents:
- Subject: `"Play Zynpath with <DisplayName>"`
- Body: `"Join me on Zynpath: Number Path Puzzle! Add me using my Zynpath ID: ZYN-XXXX-YYYY or tap: https://zynpath.com/invite?id=ZYN-XXXX-YYYY"`
- Invokes native Android Sharesheet (`Intent.createChooser`).

---

## 5. Friend Duel Real-Time Invitations vs External Links (Prompt 22)
- **External Discovery Links:** External links (`zynpath://invite?id=ZYN-...`) are used solely for initial friend discovery and adding accepted friends.
- **In-App Real-Time Duel Invitations:** Once friendship is established, Friend Duels are initiated directly via authenticated real-time WebSocket messaging and REST API (`POST /api/v1/multiplayer/friend-duel/invitations/send`).
- **Targeted Delivery:** Duel invitations contain an ephemeral backend-issued `invitationId` (60s lifetime) and are delivered exclusively to the friend's active session. External deep links cannot bypass the mutual friendship check required for Friend Duels.

