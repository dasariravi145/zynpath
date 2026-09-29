# Game Mode Discovery & Availability

## Overview

Zynpath features five distinct game modes, each serving a specific player motivation ranging from relaxed offline logic puzzle-solving to real-time competitive duels and multiplayer party tournaments.

---

## The Five Modes

### 1. Solo Worlds (Campaign)
- **Nature:** Core offline puzzle progression.
- **Content:** 300 base logic levels across 6 canonical worlds.
- **Availability:** 100% offline ready. No internet or login required.
- **Rules:** No timers, no advertisements, no move penalties. Hints available with star rating tradeoffs.

### 2. Daily Challenge
- **Nature:** Daily ritual puzzle with a global community.
- **Reset:** Canonical midnight UTC daily reset. Identical for all time zones.
- **Offline Behavior:** Fully playable offline. Solve records are saved locally and synchronized automatically to the global leaderboard once network connectivity is re-established.
- **Streaks:** Preserved across local play; verified upon backend synchronization.

### 3. Quick Duel (1v1)
- **Nature:** Real-time competitive matchmaking.
- **Matching:** Automatically pairs two players within the same rating bracket.
- **Fairness:** Both players receive the exact same puzzle seed, grid dimensions, and number sequence.
- **Network Requirement:** Online required. When offline, the card displays `"Requires Internet"` and tapping triggers a helpful explanation modal.

### 4. Friend Duel (1v1)
- **Nature:** Private invitation-based match.
- **Entry:** Room code, direct deep link (`zynpath://friend_duel`), or friend list invite.
- **Network Requirement:** Online required.

### 5. Mini League
- **Nature:** Private party tournament for 2–5 players.
- **Structure:** Multi-round competitive format with cumulative standings.
- **Network Requirement:** Online required. Strictly capped at 5 players.

---

## Offline & Online Availability Architecture

- **Never Hide Online Modes:** Rather than hiding cards when the device goes offline, cards remain visible to communicate feature existence while displaying clear status badges (`"Requires Internet"` vs `"Online Matchmaking"`).
- **Graceful Feedback:** Tapping an online mode while offline displays an informative modal:
  `"Multiplayer duels require an active internet connection. Offline Solo and Daily Challenge are ready to play anytime."`
- **Guest-First Policy:** Guest accounts can play Solo and Daily Challenge without signing in. Multiplayer modes offer Google Sign-In or account linking when needed, preserving all guest progress non-destructively.
