# EXACT UI SCREEN AUDIT — ZYNPATH REFERENCE AUDIT

**Date:** 2026-09-29  
**Platform:** Android (Jetpack Compose)  
**Authoritative References:** `D:\Zynpath\assets\references\01_splash_1080x1920.png` through `12_multiplayer_result_1080x1920.png` and `D:\Zynpath\assets\reference_composite.png`.

---

## 1. Executive Summary

This audit compares the approved visual design references (`01` through `12`) with the current Android Compose implementation. It identifies the root cause of physical-device issues—including duplicate Splash branding, oscillating loading bars, broken text constraints, and missing reference compositions—and establishes the authoritative roadmap for exact screen replacement.

---

## 2. Screen-by-Screen Reference Audit

| Screen # | Reference File | Screen Name | Current Android State | Target Reference Composition | Identified Root Cause / Mismatches |
|---|---|---|---|---|---|
| **01** | `01_splash_1080x1920.png` | **Splash Screen** | Duplicate branding, duplicate tagline, duplicate loading bars, progress oscillating 0.35–0.85 backwards via `RepeatMode.Reverse`. | Single ZYNPATH 3D logo, single subtitle, single tagline, stepping stone path, single clean capsule progress bar (forward-only 0–100%), single "Loading..." text. | `SplashScreen.kt` rendered `ZynpathCinematicLogo`, Compose tagline, and a Compose progress bar right on top of `bg_splash_reference.png`, which already contained baked-in text and loading bar. |
| **02** | `02_login_1080x1920.png` | **Login / Welcome** | Custom animated canvas or partially cropped hero card; missing direct navigation from startup when auth required. | Boy and girl adventurers with backpacks looking across mountain valley with 2 cyan stepping stones; Facebook button; Guest button; 2-line feature subtitle. | Artwork cropped with simulated device frame in `bg_login_hero.png`; Compose buttons need exact colors, pill heights, and clean artwork separation. |
| **03** | `03_home_1080x1920.png` | **Home Game Hub** | Hero artwork confined to a small 230dp card (`bg_home_hero`) leaving large void below mode cards; generic icons instead of reference icons. | Full fantasy floating-island composition with glowing stepping stones (1, 2, 4, 5) leading to trophy; Gold "▶ PLAY" pill; 3 compact mode cards (Quick Duel, Friends Arena, Daily Challenge); 5-tab bottom navigation. | The screen did not use the full vertical island composition as its background; mode cards and HUD spacing did not match reference proportions. |
| **04** | `04_level_selection_1080x1920.png` | **Level Selection** | Text-heavy cards in some flows; grid tiles lacking exact glowing border, lock, and star layout. | Clean blue header with back and forward arrows; Gold "World 1" active tab with royal blue inactive tabs (horizontally scrollable across 6 worlds); 4-column glowing level tiles with stars/locks; bottom scenic lake & island landscape. | Reused generic level card components rather than the 4-column reference grid tiles and scenic footer. |
| **05** | `05_gameplay_1080x1920.png` | **Solo Gameplay** | Generic header and action buttons; forest background replaced by duplicate scenic image. | Pause button (left), "Level 8" title, wand icon (right); 3-star score progress bar; stopwatch timer "00:28"; cyan glowing 4x5 grid with connected paths; 3 bottom action cards (Hint, Shuffle/Reset, Undo) with red badges. | `bg_gameplay_scene.png` was a duplicate of `bg_level_scenic.png`; bottom action bar did not match the 3 rounded square cards with red badge counts. |
| **06** | `06_rewards_1080x1920.png` | **Rewards Hub** | Cropped chest artwork, text overflow in daily items, mismatched tabs. | Coin & Star HUD, "REWARDS" title; Daily / Missions / Achievements tabs (gold active pill); Gift illustration card; 5 daily tiles (Day 1 check, Day 2 gold highlight, Day 3 star, Day 4 coin, Day 5 gift); Glowing treasure chest on stone platform; vibrant green "Claim" button. | Tab styles diverged from gold/blue pills; chest artwork was cropped without glow platform; tile rows suffered from text wrapping. |
| **07** | `07_friends_1080x1920.png` | **Friends Hub** | Inconsistent headers, cropped avatars, missing Facebook card hierarchy. | Header with back arrow and "FRIENDS"; "Facebook Friends" (gold pill) and "Invitations" tabs; "Connect with Facebook" blue banner card with "Connect Facebook" button; "Suggested Friends" list with avatars, status subtitles, and blue "Invite" buttons. | Avatars were not sized to reference (48dp); row padding caused text compression; tab styling differed. |
| **08** | `08_create_room_1080x1920.png` | **Create Game Room** | Blank spacing, oversized inputs, misaligned player selector. | "Create Game Room" header; 1–5 player selector buttons (cyan active highlight) with "(Max 5)" subtitle; "Invite Friends" card with '+' icon; "Room Settings" (Level Selection, Game Mode); Gold "Create Room" pill button. | Room capacity was not formatted in 5 discrete rounded buttons; settings rows had non-standard styling. |
| **09** | `09_invite_friends_1080x1920.png` | **Invite Friends** | Search bar and checkboxes clipped on small screens. | "Invite Friends" header; rounded search input ("Search Facebook Friends..."); friend list with circular avatars, names, and cyan checkmark selection circles; Gold pill button "Send Invitation (X)". | Checkbox alignment and list item height did not match reference 64dp cards. |
| **10** | `10_waiting_room_1080x1920.png` | **Waiting Room Lobby** | Missing 3+2 avatar grid layout, room code badge clipped. | "Game Room" header with "Room Code / ZP4587" copy badge; 3 player slots on top (Host with cyan glow, Ready players with green text), 2 '+' slots on bottom ("Invite Friend (4/5)", "(5/5)"); "Waiting for players..."; "Start Game" button. | The 5 slots were rendered in a single horizontal scroll or generic list rather than the exact 3-top, 2-bottom reference cluster. |
| **11** | `11_multiplayer_gameplay_1080x1920.png` | **Multiplayer Gameplay** | Player cards stacked vertically or lacking bottom multi-player bar. | "Multiplayer - Level 5" header with timer "00:25"; central puzzle grid; 4 bottom player cards side by side with avatar, name, live score, and colored progress underline. | Layout lacked the compact 4-player horizontal card deck at bottom. |
| **12** | `12_multiplayer_result_1080x1920.png` | **Multiplayer Results** | Missing 3D podium layout, crown, or rank badges. | "Game Result" header with confetti; 3-tier podium (1st center with crown and gold pillar, 2nd silver left, 3rd bronze right) with avatars, scores, and names; 4th and 5th player rank cards below; Gold "Play Again" and Blue "Back to Home" buttons. | Results screen used a plain list without the reference 3-tier podium composition. |
| **13** | Physical Screenshot | **Solo Victory Screen** | Text wrapping vertically 1 character per line in "REWARDS & UNLOCKS" badges; NEXT WORLD showing prematurely. | Clean 3-star banner; properly wrapped and sized reward badges; NEXT LEVEL for normal levels, NEXT WORLD only for final level of World. | 4 badges with `weight(1f)` forced badge widths below 70dp, compressing text into vertical character stacks. |
| **14** | Physical Screenshot | **Profile Screen** | "Link Account" / "Secure guest progress" text rendering vertically 1 character per line. | Responsive profile card with unclipped text, proper flex layout, no 1-character vertical wrapping. | `Row` with nested `weight` and unconstrained text columns caused extreme horizontal compression. |

---

## 3. Critical Root Cause Findings

### 3.1 Duplicate Splash Branding & Loading
- **Root Cause:** `SplashScreen.kt` placed `bg_splash_reference.png` as its background image via `Image(painter = painterResource(R.drawable.bg_splash_reference))`. Because that reference image already has the ZYNPATH 3D logo, subtitle, tagline, loading capsule, and "Loading..." text baked into the pixels, the additional Compose widgets rendered directly over it, creating a jarring double image on any device whose aspect ratio differs from 9:16.
- **Root Cause of Backward Progress:** `SplashScreen.kt` lines 191–199 used `infiniteRepeatable(tween(1500), repeatMode = RepeatMode.Reverse)` animating between 0.35f and 0.85f, making the progress bar bounce backward and forward indefinitely.

### 3.2 Single-Character Vertical Text Wrapping
- **Root Cause:** In `GameRewardBadge.kt` and `ProfileScreen.kt`, horizontal rows placed multiple items with `Modifier.weight(1f)` without minimum width constraints. Inside each item, a fixed 36–42dp icon plus 10–12dp spacer plus 24dp padding consumed 70–80dp of space. On 360–390dp phone screens, the remaining width for the text column was only 5–15dp, forcing Compose text layout to break at every single character (`W\nO\nR\nL\nD...`).

---

## 4. Execution Plan
1. Reconstruct clean backgrounds and live Compose controls so artwork contains no baked-in dynamic text or loading bars.
2. Fix Splash screen to single live progress bar (monotonic forward-only) and single loading label, with no duplicate logo/tagline overlay.
3. Update `SplashViewModel` and `NavGraph` to restore session, inspect authentication, navigate cleanly with back stack removal, and never allow Back button to reopen Splash.
4. Replace Home, Level Selection, Gameplay, Rewards, Friends, Room, and Multiplayer screens with exact reference layouts.
5. Fix reward badges and profile rows to prevent 1-character-per-line text wrapping.
6. Verify via automated navigation and startup tests.
