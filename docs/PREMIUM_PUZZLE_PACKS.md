# Premium Solo Puzzle Packs

## Overview

The Premium Solo Puzzle Pack system in **Zynpath: Number Path Puzzle** introduces curated, thematic collections of advanced logic challenges designed specifically for dedicated solvers seeking greater difficulty, unique geometric patterns, and intricate edge constraints beyond the canonical campaign.

In accordance with Zynpath's foundational fairness principles, **Premium Packs are completely optional Solo content**. They do not modify, replace, or truncate the 300 free campaign levels across Worlds 1–6, and they never confer any competitive scoring, rating, or timing advantages in multiplayer duels, party leagues, or the Daily Challenge.

---

## Pack Catalog & Identity

Each Premium puzzle pack possesses a stable, versioned identifier and explicit metadata:

| Pack Identifier | Display Name | Difficulty Band | Puzzle Count | Publication Status | Description |
|---|---|---|---|---|---|
| `pack_master_serpentine` | Serpentine Mastery | `EXPERT` | 5 | `PREMIUM` (Playable) | Winding orthogonal paths through high-density checkpoints demanding strict foresight. |
| `pack_labyrinth_walls` | Labyrinth Walls | `MASTER` | 5 | `PREMIUM` (Playable) | Constrained corridor puzzles featuring complex blocked edges and bottleneck deductions. |
| `pack_grandmaster_7x7` | Grandmaster 7x7 | `GRANDMASTER` | 0 (Upcoming) | `COMING_SOON` | 49-cell Hamiltonian circuits reserved for ultimate path-solving masters. |

### Publication Statuses

- **`FREE`**: Pack available to all players regardless of entitlement.
- **`PREMIUM`**: Requires an active `PREMIUM_SOLO_PACKS` subscription entitlement (or valid bounded offline cache).
- **`UNAVAILABLE`**: Pack withdrawn or disabled by configuration.
- **`COMING_SOON`**: Unreleased collection; metadata visible to players with zero fabricated puzzles or counts.

---

## Canonical Puzzle Compliance

All puzzles included in Premium packs strictly adhere to the canonical rules of Zynpath:

1. Start at numbered checkpoint 1.
2. Advance through checkpoints in ascending sequence ($1 \to 2 \to \dots \to N$).
3. Move exclusively through orthogonal cell boundaries.
4. Cover 100% of required grid cells exactly once.
5. Forward movement may never revisit an already covered cell.
6. Path edges cannot cross blocked wall boundaries.
7. Reaching the final checkpoint early is not a victory; all required cells must be covered.
8. No game mechanics deviate from core Zynpath (no tile matching, gravity, refills, or pay-to-win boosts).

---

## Solver Verification & Integrity Pipeline

Every shipped puzzle definition is validated through the offline/authoritative solver pipeline before entering distribution:

1. **Structural Validation**: Verified by `PuzzleDefinitionValidator` (coordinate bounds, start checkpoint #1 presence, ascending sequential order, valid planar edge coordinates).
2. **Deterministic Fingerprinting**: Canonical SHA-256 fingerprint generated via `PuzzleFingerprint.computeSha256(def)`.
3. **Hamiltonian Solver Proof**: Verified by `PuzzleSolver.solve(def)` to guarantee at least one valid complete solution exists within standard node/time budgets.
4. **Manifest Checksum**: Pack manifests compute content checksums to detect corrupted or tampered downloads.

---

## Progression & Completion Tracking

Player progress on Premium packs is isolated from the 300-level free campaign:

- Progress is persisted locally in the Room table `premium_pack_progress` with primary key `(playerId, packId, levelIndex)`.
- Tracks:
  - Completion status (`isCompleted`)
  - Personal best solve time (`bestSolveTimeMs`)
  - First completion timestamp (`completedAt`)
- Pack progression is sequential: Level 1 is unlocked upon pack access; Level $N$ unlocks upon completing Level $N-1$.
- Completed levels remain available for replay at any time to improve personal best times.

---

## Subscription Expiration & Resubscription

- **On Expiration**:
  - Downloaded pack files are safely preserved on disk.
  - Completed levels, personal best times, and star counts are permanently preserved in the local database.
  - Access to launch uncompleted or new pack levels transitions to `LOCKED`.
  - The UI presents a clear "Subscription Required" card prompting reactivation.
- **On Resubscription**:
  - Full access to all entitled packs is immediately restored.
  - All previously completed levels and best times remain intact without progress reset or duplicate records.
