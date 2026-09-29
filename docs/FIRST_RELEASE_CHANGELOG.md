# Zynpath: Number Path Puzzle — First Release Changelog

**Release Version:** `1.0.0`  
**Build Code:** `1`  
**Release Date:** September 2026  
**Status:** Authoritative Release Notes & Changelog  

---

## 1. Google Play Store Release Notes (`<en-US>`)

```xml
<en-US>
Welcome to Zynpath: Number Path Puzzle — One path. Every number!

• 300 HANDCRAFTED LEVELS: Explore 6 unique worlds from serene 4x4 grids up to challenging 8x8 master mazes with complex wall obstacles.
• DAILY CHALLENGE: Solve a new deterministic puzzle every day at midnight UTC and build your global streak.
• REAL-TIME MULTIPLAYER: Compete head-to-head in 1v1 Quick Duels, invite friends to private matches, or race up to 5 players in Mini Leagues!
• 100% OFFLINE & GUEST-FIRST: Play immediately without account creation; all 300 solo levels are playable offline with zero network required.
• ACCESSIBLE DESIGN: Full TalkBack screen reader support, non-color visual indicators, high-contrast themes, and an optional tap-to-move input mode.
• FAIR & HONEST: Zero pay-to-win mechanics, zero forced video ads during gameplay, and strict anti-cheat server validation.
</en-US>
```

---

## 2. Engineering Changelog (v1.0.0 Milestone)

### Core Puzzle Engine & Content
* **Deterministic Engine:** Pure Kotlin puzzle engine enforcing strictly orthogonal moves, ascending checkpoint ordering ($1 \to 2 \to \dots \to N$), and 100% full-grid coverage win conditions.
* **300 Solvable Levels:** Curated catalog across 6 distinct progression worlds:
  * World 1 (Levels 1–20): $4 \times 4$, 4–6 checkpoints, 0 walls.
  * World 2 (Levels 21–50): $5 \times 5$, 4–7 checkpoints, 0 walls.
  * World 3 (Levels 51–100): $5 \times 5$, 4–7 checkpoints, 1–5 walls.
  * World 4 (Levels 101–150): $6 \times 6$, 4–8 checkpoints, 2–8 walls.
  * World 5 (Levels 151–200): $7 \times 7$, 4–10 checkpoints, 4–12 walls.
  * World 6 (Levels 201–300): $8 \times 8$, 4–12 checkpoints, 6–18 walls.
* **Interactive Tutorial:** 7-stage interactive onboarding engine with step-by-step move rejection explanations and sandboxed recovery.

### Multiplayer & Online Competition
* **Quick Duel (1v1):** Real-time matchmaking queue with synchronized 3-second countdown and live progress HUD.
* **Friend Duel (1v1):** Private room creation with collision-resistant 6-character room codes and alternative puzzle rematch loops.
* **Mini League (2–5 Players):** Multiplayer lobby racing with dynamic room capacities and a 45-second finishing window on first place.
* **Authoritative Server Verification:** Server-side `ServerPuzzleValidator` re-simulates the full path sequence; solve times are calculated strictly from server receipt timestamps.
* **Curated Reactions:** In-game reaction drawer with 8 preset emojis and sportsmanship phrases; zero persistent chat storage or moderation liability.

### Persistence, Identity & Cloud Sync
* **Local Persistence:** Room Database schema version `11` (`MIGRATION_10_11`) and Jetpack DataStore preferences for audio, haptics, and theme settings.
* **Guest-First & Social Auth:** Anonymous guest UUID protected by Android KeyStore AES-256-GCM; optional linking to Google Sign-In and Facebook Login without losing offline progress.
* **Offline-to-Online Sync:** Durable operation queue synchronizing level completions, personal bests, and daily streaks when reconnecting.
* **Self-Service Account Deletion:** In-app one-click deletion and external web portal with automatic Google Play subscription warnings.

### Monetization & Customization
* **Google Play Billing 7.1.1:** Premium subscriptions (₹99/month, ₹499/6-months) granting an ad-free experience, unlimited solo hints, and premium analytics.
* **Optional Rewarded Ads:** Google Mobile Ads (AdMob v23.6.0) for extra solo hints (capped at 5/day, wallet max 10) with Server-Side Verification (SSV). Zero ads in active gameplay.
* **Cosmetic Personalization:** 5 board themes (Classic Dark, Paper Light, Cyber Neon, Slate Minimal, Autumn Glow), 4 canvas path drawing effects, and 4 profile avatar frames.

### Accessibility, Performance & Security
* **Accessibility (WCAG 2.1 AA):** All interactive targets $\ge 48 \times 48\text{ dp}$; normal text contrast $\ge 4.5:1$; graphical wall contrast $\ge 3.0:1$; TalkBack virtual grid semantics; discrete tap-to-move input.
* **Responsive Layouts:** Adaptive layouts across compact phones, tablets, and foldables with non-linear font scaling up to 200%.
* **Security Hardening:** Deny-by-default API security (`@RequireAccess`), BOLA/IDOR protection, cleartext traffic rejected (`cleartextTrafficPermitted="false"`), and zero committed secrets.
