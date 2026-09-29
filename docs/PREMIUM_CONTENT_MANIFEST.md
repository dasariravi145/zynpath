# Premium Content Manifest Specification

## Overview

The **Premium Content Manifest** defines the authoritative schema used by Zynpath to describe, verify, version, and distribute Premium Solo puzzle collections.

---

## Manifest Schema

```json
{
  "packId": "pack_master_serpentine",
  "packVersion": 1,
  "displayName": "Serpentine Mastery",
  "description": "Advanced winding paths through high-density checkpoints demanding strict orthogonal foresight.",
  "difficulty": "EXPERT",
  "puzzleCount": 5,
  "requiredEntitlement": "PREMIUM_SOLO_PACKS",
  "checksum": "serpentine_v1_c7a91f4e",
  "puzzles": [
    {
      "puzzleId": "premium_serpentine_01",
      "puzzleVersion": 1,
      "fingerprint": "d027e02e1c94d07bfd21fcbb41cb2b19cfaf116630f9a26322ad4e4334a12361",
      "levelIndex": 1,
      "gridSize": "5x5",
      "checkpointCount": 5
    }
  ]
}
```

### Field Definitions

| Field | Type | Description |
|---|---|---|
| `packId` | `String` | Unique, stable identifier for the pack. |
| `packVersion` | `Int` | Monotonically increasing schema/content version number. |
| `displayName` | `String` | Localized, user-facing pack title. |
| `description` | `String` | Curated summary of pack themes and mechanics. |
| `difficulty` | `String` | Categorical difficulty classification (`BEGINNER`, `INTERMEDIATE`, `ADVANCED`, `EXPERT`, `MASTER`, `GRANDMASTER`). |
| `puzzleCount` | `Int` | Exact number of playable puzzles in the collection. |
| `requiredEntitlement` | `String` | Key of the required subscription feature (defaults to `PREMIUM_SOLO_PACKS`). |
| `checksum` | `String` | Content-derived integrity hash for detecting transmission corruption. |
| `puzzles` | `List<PremiumPuzzleRef>` | Ordered list of puzzle descriptors within the pack. |

---

## Puzzle Reference (`PremiumPuzzleRef`)

Each puzzle referenced in a manifest provides minimal metadata required for level selection without disclosing complete solver solutions:

- `puzzleId`: Stable puzzle identity string.
- `puzzleVersion`: Version of the puzzle definition.
- `fingerprint`: Canonical SHA-256 hash of the canonical puzzle definition.
- `levelIndex`: 1-based sequential position within the pack.
- `gridSize`: Human-readable dimensions (e.g., `5x5`, `6x6`).
- `checkpointCount`: Number of numbered checkpoints contained in the grid.

---

## Manifest Versioning & Immutability

1. **Version Immutability**: Once a pack version is published, its constituent puzzle identities and fingerprints are immutable.
2. **Pack Updates**: Material changes to puzzle geometry or checkpoint placement require incrementing `packVersion` and assigning distinct `puzzleId` or `puzzleVersion` tags.
3. **Completion Preservation**: Historical completion records in Room reference `(playerId, packId, levelIndex, puzzleId, puzzleVersion)`. Existing player completion records remain valid across updates.
