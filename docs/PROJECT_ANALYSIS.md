# Zynpath Project Analysis & Inception Audit

**Date:** September 2026  
**Document Status:** Approved & Authoritative  
**Target:** Zynpath: Number Path Puzzle  

---

## 1. Executive Summary

This document captures the baseline inspection of the repository, environment capabilities, identity migration, architectural boundaries, and fundamental technical decisions for **Zynpath: Number Path Puzzle** (formerly referred to in early planning as MIND CHAIN).

---

## 2. Workspace & Environment Inspection

### 2.1 Workspace Audit
- **Repository Root:** `d:\Zynpath`
- **Initial State:** Empty directory upon prompt start.
- **Git Status:** Initialized clean Git repository (`.git`).
- **Adjacent Workspace Review:** Inspected `D:\` and `D:\repos`. Confirmed that no legacy MIND CHAIN code, conflicting modules, or unmigrated assets exist in the workspace root. All unrelated projects in other directories (`D:\repos`, `D:\Vyxentra`, etc.) are isolated and untouched.

### 2.2 System & Toolchain Capabilities
An automated environment scan revealed full readiness for modern native Android and enterprise Java backend development:

| Component | Detected Version / Path | Status |
|---|---|---|
| **Operating System** | Windows 10/11 x86_64 | Confirmed |
| **Java Runtime** | Java 17.0.12 LTS (`build 17.0.12+8-LTS-286`) | **Ready** (Target: Java 17+ LTS) |
| **JAVA_HOME** | `C:\Program Files\Java\jdk-17` | Configured |
| **Android SDK Root** | `C:\Users\ADMIN\AppData\Local\Android\Sdk` | **Ready** |
| **Android Platforms** | `android-35`, `android-36`, `android-36.1` | Installed |
| **Android Build Tools** | `35.0.0`, `36.0.0`, `36.1.0`, `37.0.0` | Installed |
| **Android Platform Tools** | `C:\Users\ADMIN\AppData\Local\Android\Sdk\platform-tools\adb.exe` | Installed |
| **Terminal / Shell** | PowerShell (Windows) | Operational |

---

## 3. Brand Identity & Rebranding Analysis

### 3.1 Migration from "MIND CHAIN" to "Zynpath"
- **Working Title:** MIND CHAIN (early conceptual notes).
- **Final Official Title:** **Zynpath: Number Path Puzzle**
- **Tagline:** *One path. Every number.*
- **Package Identifier:** `com.zynpath.puzzle` (clean namespace, eliminating legacy conflicts).
- **Branding Palette:** 
  - Primary Dark / Background: Midnight Navy (`#0B132B`, `#1C2541`)
  - Accent / Forest Depth: Deep Emerald (`#2E5D4B`, `#48A9A6`)
  - Grid Board: Crisp Off-White / Pale Slate (`#F4F7F6`, `#E2E8F0`)
  - Checkpoint Circles: High-contrast Dark Indigo (`#0B132B`) with crisp White numerals
  - Active Path: Luminescent Cyan / Electric Mint (`#00F5D4`, `#48CAE4`)
  - Blocked Walls: Crimson Ember (`#E63946`)

---

## 4. Architectural Boundaries & Paradigm Shifts

Previous draft ideas or generic puzzle concepts have been formally reviewed and refined to adhere to strict engineering, UX, and cost constraints:

### 4.1 Strict Mechanic Definition
- **Pure Continuous Orthogonal Path**: The player draws an unbroken line connecting cell centers.
- **Prohibited Mechanics**: No tile matching, no tile popping, no falling blocks, no gravity, no number merging (2048-style), and no combo mechanics.
- **Independent Dual Validation**:
  1. Ascending checkpoint traversal ($1 \to 2 \to \dots \to N$).
  2. 100% cell coverage (every required cell covered exactly once).
  Both conditions are mandatory for puzzle completion.

### 4.2 Guest-First Flow
- Zero friction on launch. Players immediately enter Solo Play without an account.
- Room and DataStore maintain complete offline state.
- Account creation (Google / Facebook) is strictly requested only when online features (Duels, Leagues, Global Leaderboards) are accessed.
- Progress from guest play is seamlessly merged upon account linking.

### 4.3 Elimination of Text Chat & Storage Bloat
- Older ideas suggesting full text chat with cloud backups have been **completely eliminated**.
- Replaced by a high-efficiency **Temporary Reaction System**:
  - Only curated emojis and preset strings ("Wow!", "Nice!", "GG!", "Well played!", "Good luck!", "Amazing!", "Rematch!").
  - Processed ephemerally in server RAM over WebSocket sessions.
  - Zero database tables, zero moderation overhead, zero persistent chat storage costs.

### 4.4 Cost Discipline
- Solo gameplay is **100% offline and client-side**. No server calls during level generation, puzzle solving, or drawing.
- **Zero Finger-Coordinate Streaming**: In multiplayer, only high-level events (match start, progress milestones such as cell coverage percentage, level completion claim, and validation payloads) are transmitted. Raw touch vectors are never sent to the backend.
- Ephemeral game room state in Java heap memory ensures zero unnecessary database transactions during active matches.

### 4.5 Monetization Calibration
- **AdMob**: Non-intrusive banners on non-gameplay screens; interstitially paced level transitions; rewarded ads exclusively for solo hints. Never show ads during active puzzle solving or competitive multiplayer.
- **Google Play Billing**: Premium tiers at ₹99/month and ₹499/6-months. Prices are queried dynamically from the Play Store rather than hardcoded in client code.
- **Competitive Integrity**: In competitive modes (Duels and Mini Leagues), hints are disabled for both Free and Premium users.
