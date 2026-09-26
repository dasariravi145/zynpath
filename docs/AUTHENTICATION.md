# Zynpath Authentication & Identity Architecture

**Status:** Authoritative  
**Philosophy:** Guest-First, Frictionless Onboarding, Seamless Account Linking  

---

## 1. The Guest-First Onboarding Flow

Zynpath strictly avoids mandatory sign-in walls. Players must be able to install and enjoy the game immediately without disclosing personal identity, email addresses, or social accounts.

```mermaid
graph TD
    A[Install Application] --> B[Launch Zynpath]
    B --> C[Instant Splash / Intro Animation]
    C --> D[Initialize Anonymous Guest ID (UUIDv4)]
    D --> E[Solo Play Home Screen]
    E --> F[Play Offline Campaign (Worlds 1-6)]
    
    F -.->|User taps Online Feature\nQuick Duel / League / Global Rank| G{Authenticated?}
    G -- Yes --> H[Enter Online Mode]
    G -- No --> I[Present Sign-In Modal]
    I -->|Continue with Google| J[Google OAuth 2.0]
    I -->|Continue with Facebook| K[Facebook Login SDK]
    I -->|Cancel| E
    
    J --> L[Firebase Auth Account Linking]
    K --> L
    L --> M[Preserve & Merge Local Guest Progress to Cloud Profile]
    M --> H
```

---

## 2. Guest Player Capabilities & Local Storage

Guest players possess complete operational autonomy for all offline content:
- **Campaign Progression**: Complete all 300 levels across Worlds 1–6.
- **Game Tools**: Full access to undo, reset, and solo hints.
- **Local Persistence**: All stars, best completion times, and statistics are stored locally in Room.
- **Preferences**: Theme, haptics, and audio volumes are persisted in DataStore.
- **Zero Network Requirement**: The app operates with airplane mode enabled.

---

## 3. Account Linking & Progress Preservation

When a guest opts into online multiplayer or global leaderboards, their local progress is safely linked to their authenticated identity:

### 3.1 Migration Algorithm
1. Retrieve local anonymous stats:
   - Completed level IDs, star ratings, best times.
   - Total hint balance and achievement flags.
2. Complete OAuth handshake (Google ID Token or Facebook Access Token).
3. Call Firebase Auth `currentUser.linkWithCredential(credential)`.
4. If successful:
   - The user's UID remains identical, automatically retaining cloud claims.
   - Sync local Room records with remote profile database via batch upsert:
     $$\text{Final Stars}(\text{Level } L) = \max(\text{LocalStars}_L, \text{CloudStars}_L)$$
     $$\text{Final Time}(\text{Level } L) = \min(\text{LocalTime}_L, \text{CloudTime}_L)$$
5. If the account already exists (e.g., player logging into an existing Google account on a new phone):
   - Resolve conflicts in favor of the higher level progression or prompt player to choose.

---

## 4. Friend Discovery & Identity Model

### 4.1 Zynpath Identity Attributes
Every player is assigned an unambiguous, readable Zynpath identity:
- `playerId`: Unique alphanumeric identifier (e.g., `ZYN-7749-8102`).
- `username`: Customizable display name (e.g., `SwiftSolver`).
- `avatarUrl`: Selected preset avatar icon or profile image.

### 4.2 Facebook Friends Constraint Mitigation
> **CRITICAL ARCHITECTURAL NOTE:**  
> Facebook Login **does not** automatically expose the user's entire Facebook friends list. Facebook's Graph API (`user_friends`) only grants access to friends who have *both* installed the app *and* granted the permission.

**Zynpath Friend Resolution Model**:
1. **Zynpath Player Tag Search**: Players can search for friends by their unique Zynpath Player ID (`ZYN-XXXX-XXXX`).
2. **Deep-Link Room Invites**: Shareable cryptographic invite links (e.g., `https://zynpath.com/join?room=8841-f92a`) that open Zynpath directly into a Friend Duel or Mini League room via Android App Links.
3. **QR Code Sharing**: On-screen QR code for instant local friend pairing.
4. **Recent Opponents**: Option to send a friend request directly from post-match Duel results.

---

## 5. Backend JWT Verification Filter

All incoming WebSocket handshakes and REST calls to authenticated endpoints (`/api/v1/duel/*`, `/api/v1/league/*`) pass through a Spring Security Filter that verifies the Firebase JWT token:

```java
// Conceptual Spring Security JWT verification
FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(bearerToken);
String uid = decodedToken.getUid();
SecurityContextHolder.getContext().setAuthentication(
    new ZynpathUserAuthentication(uid, decodedToken.getEmail())
);
```
Unauthenticated or expired tokens are rejected with HTTP 401 / WebSocket close code `4401`.
