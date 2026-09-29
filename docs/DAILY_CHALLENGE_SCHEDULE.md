# Zynpath Daily Challenge Schedule Specification

**Status:** Authoritative  
**Domain:** Deterministic Offline Schedule Table & Puzzle Pool Mapping  

---

## 1. Schedule Specification

- **Schedule Version:** `1.0.0`
- **Hash Algorithm:** Cryptographic SHA-256 over `"$scheduleVersion:$dateKey"`
- **Index Function:** `(ByteBuffer.wrap(hash).getInt() and 0xFFFFFFFFL) % poolSize`
- **Pool Capacity:** 14 Curated Verified Puzzles
- **Cycle Periodicity:** Uniformly distributed pseudo-random selection over curated pool.

---

## 2. Packaged Puzzle Pool Index

| Pool Index | Puzzle ID | Grid Size | Checkpoints | Blocked Edges | Difficulty | Title |
|---|---|---|---|---|---|---|
| **0** | `w1_lvl1` | 4×4 | 5 | 0 | Beginner | Serpentine Spark |
| **1** | `w2_lvl21` | 5×5 | 6 | 0 | Medium | Emerald Meadow |
| **2** | `w3_lvl51` | 5×5 | 6 | 2 | Challenging | Granite Gate |
| **3** | `w1_lvl2` | 4×4 | 5 | 0 | Beginner | Corner Weaver |
| **4** | `w2_lvl22` | 5×5 | 6 | 0 | Medium | Vertical Cascade |
| **5** | `w3_lvl52` | 5×5 | 6 | 2 | Challenging | Double Barrier |
| **6** | `w1_lvl3` | 4×4 | 6 | 0 | Beginner | Ascent Route |
| **7** | `w2_lvl23` | 5×5 | 6 | 0 | Medium | Spiral Sweep |
| **8** | `w3_lvl53` | 5×5 | 6 | 3 | Hard | Triple Bastion |
| **9** | `w1_lvl4` | 4×4 | 5 | 0 | Beginner | Crosswind |
| **10** | `w1_lvl5` | 4×4 | 5 | 0 | Beginner | Diagonal Sweep |
| **11** | `daily_curated_6x6_01` | 6×6 | 7 | 0 | Expert | Hex Grid Odyssey |
| **12** | `w2_lvl21` | 5×5 | 6 | 0 | Medium | Verdant Echo |
| **13** | `w3_lvl51` | 5×5 | 6 | 2 | Challenging | Stone Fortress |

---

## 3. Sample Date Resolution Table

| UTC Date Key | Pool Index | Puzzle ID | Difficulty Tier | Challenge Title |
|---|---|---|---|---|
| `2026-09-25` | 3 | `w1_lvl2` | Beginner | Corner Weaver |
| `2026-09-26` | 7 | `w2_lvl23` | Medium | Spiral Sweep |
| `2026-09-27` | 1 | `w2_lvl21` | Medium | Emerald Meadow |
| `2026-09-28` | 8 | `w3_lvl53` | Hard | Triple Bastion |
| `2026-09-29` | 11 | `daily_curated_6x6_01` | Expert | Hex Grid Odyssey |
| `2026-09-30` | 2 | `w3_lvl51` | Challenging | Granite Gate |

---

## 4. Invariance Guarantees

1. **Client Independence:** Whether running on an Android phone, emulated test runner, or headless server validator, the same date key maps to the exact same puzzle ID.
2. **Immutability:** Once published with schedule version `1.0.0`, past challenge assignments must remain permanent to avoid corrupting historical user solve records.
