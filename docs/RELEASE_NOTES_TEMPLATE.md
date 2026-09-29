# Release Notes Specification & Templates

## 1. Google Play Store "What's New" Format (`<en-US>`)
Google Play Console requires release notes to be under 500 characters per localized language.

### Production Release Template (`release_notes_en_US.txt`):
```xml
<en-US>
Welcome to Zynpath: Number Path Puzzle!
• One path. Every number. Connect checkpoints in ascending order and cover 100% of the grid!
• 300 Handcrafted Solo Levels across 6 unique worlds: Origins, Corridors, Labyrinths, Fortresses, Monoliths, and Infinite Nexus.
• Real-time Competitive Multiplayer: 1v1 Quick Duels, Friend Duels, and 2-5 player Mini Leagues.
• Daily Challenge: A fresh, solver-verified puzzle every day at midnight UTC.
• 100% offline solo gameplay with zero mandatory ads or logins.
</en-US>
```

---

## 2. Internal & Closed Testing Release Notes Template
Used for distribution to internal QA testers and closed testing participants:

```markdown
# Zynpath Release Notes - v1.0.0 (Build 1)

**Date:** 2026-09-27  
**Version Name:** 1.0.0  
**Version Code:** 1  
**Git Commit:** [COMMIT_SHA]  
**Target Track:** Internal Testing / Closed Testing  

### Highlights & Features Included:
- **Solo Campaign:** 300 offline, deterministic levels with automatic local progress persistence.
- **Pure Logic Puzzle Engine:** Strictly orthogonal single-line movement with ascending checkpoint validation and full board coverage requirement.
- **Online Multiplayer:** Quick Duels, Friend Duels with mutual rematch, and Mini League private rooms.
- **UTC Daily Challenge:** Deterministic shared daily puzzle with local provisional offline play and verified server leaderboard eligibility.
- **Accessibility Hardening:** Dual-layer touch board supporting TalkBack virtual cell navigation, physical keyboard/D-pad, and high-contrast non-color checkpoint states.
- **Zero-Trust Backend:** Spring Boot 3 modular monolith on PostgreSQL with versioned Flyway migrations.

### Known Limitations & Testing Focus:
- Real-time multiplayer requires active network connectivity to `https://api.zynpath.app`.
- In-app purchases run in Google Play License Testing sandbox mode.
- Notifications require explicit permission grant on Android 13+ (API 33+).

### Compatibility:
- Supported: Android 7.0 (API Level 24) through Android 16 (API Level 36).
- Form Factors: Compact Phones, Medium/Large Foldables, Landscape Tablets.
```
