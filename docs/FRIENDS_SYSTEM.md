# Zynpath Friends System

## Overview
The Zynpath Friends System establishes mutual player-to-player relationships without relying on external social media friend graphs (such as Facebook Friend permissions). It is built entirely on Zynpath's verified authentication architecture and public player identities.

---

## 1. Core Principles
- **Guest-First Preservation:** Offline Solo Play and offline Daily Challenge remain 100% playable without an account. Guest players can open the Friends screen to learn about online features, but will never see a fake or simulated friends list.
- **Mutual Agreement:** Friendships are bidirectional and require explicit invitation and acceptance.
- **Privacy by Design:** Player discovery is restricted to exact Public Zynpath ID lookup (`ZYN-XXXXXXXX`). There is no public user directory, search auto-complete, or scraping endpoint.
- **No Free-Text Chat:** In accordance with Zynpath child-safety and anti-harassment guidelines, the friends interface does not support open-ended text messaging.

---

## 2. Relationship States
Every pair of players $(A, B)$ has a well-defined relationship state:

| State | Description |
| :--- | :--- |
| `NONE` | No current relationship or pending request. |
| `OUTGOING_REQUEST` | Player $A$ sent a friend request to Player $B$; pending response. |
| `INCOMING_REQUEST` | Player $B$ received a friend request from Player $A$; awaiting action. |
| `FRIENDS` | Mutual friendship active. Presence is visible. |
| `BLOCKED` | Player $A$ has blocked Player $B$. No requests or invites allowed. |
| `SELF` | The target identifier belongs to the calling player. |

---

## 3. Storage and Integrity
In the backend database, friendships are stored in the `friend_relationships` table using a canonical ordering check constraint:
```sql
CONSTRAINT chk_canonical_friend_pair CHECK (player_id_1 < player_id_2)
```
This guarantees that reverse-direction duplicates (`(A, B)` and `(B, A)`) cannot be inserted into the database.

---

## 4. UI Implementation
The Android `FriendsScreen` provides three primary views:
1. **Friends List:** Displays all accepted friends with live presence dots (`ONLINE`, `AWAY`, `OFFLINE`, `UNKNOWN`), their Public Zynpath ID, a direct **"Duel"** action button to invite the friend into a private 1v1 match, and quick removal/blocking options.
2. **Requests Queue:** Displays pending incoming requests (with one-tap Accept and Reject buttons) and sent requests (with Cancel buttons).
3. **Find Player:** Exact-ID search bar with input format validation and instant relationship status inspection.

---

## 5. Friend Duel Integration (Prompt 22)
- **Friendship Gating:** Only players with an active `FRIENDS` status may send or receive Friend Duel invitations.
- **Direct Entry Point:** Tapping the "Duel" button on any friend card in the Friends List navigates to `Screen.FriendDuel` with the friend's target ID prefilled.
- **Block Relationship Policy:** If player $A$ blocks player $B$, all pending duel invitations between them are immediately marked `INVALIDATED`, and future invitations are blocked at the service layer.
- **Friendship Removal During Matches:** If friendship is terminated while an active duel is underway, the active match completes normally under its private room session, but future duels and rematches between the two players are permanently blocked.

---

## 6. Friend Notification Alerts (Prompt 31)
- **`FRIEND_REQUEST` Alert**: When Player A creates a valid friend request to Player B, `SocialService` invokes `NotificationService.createNotification` targeting Player B with sender display name and deep link to `friends`.
- **`FRIEND_REQUEST_ACCEPTED` Alert**: When Player B accepts the friend request, Player A receives a notification alert confirming mutual friendship.
- **Preference Controls**: Players can enable or disable friend alerts at any time in Settings.
- **Blocked Users**: Blocked players can never trigger friend request notifications.


