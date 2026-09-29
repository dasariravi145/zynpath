# Zynpath Puzzle Asset Format Specification

## 1. Overview

Zynpath puzzle assets are stored as compact, human-readable, deterministic JSON files within the application's offline assets directory (`assets/puzzles/w{worldId}/lvl{levelId}.json`).

Assets contain only immutable puzzle board topology, cell requirements, checkpoints, wall boundaries, and curation metadata. Transient gameplay state (such as active player paths, timers, and gesture coordinates) is strictly excluded.

---

## 2. Asset Schema Specification (v1.0.0)

```json
{
  "schemaVersion": "1.0.0",
  "puzzleId": "w1_lvl1",
  "puzzleVersion": 1,
  "gridDimensions": {
    "rows": 4,
    "columns": 4
  },
  "requiredCells": [
    {"row": 0, "column": 0},
    {"row": 0, "column": 1},
    ...
  ],
  "checkpoints": [
    {"number": 1, "row": 0, "column": 0},
    {"number": 2, "row": 0, "column": 3},
    ...
  ],
  "blockedEdges": [
    {
      "first": {"row": 0, "column": 1},
      "second": {"row": 1, "column": 1}
    }
  ],
  "metadata": {
    "generatorVersion": "1.0.0",
    "generationSeed": 1001,
    "difficultyEstimate": 0.1,
    "difficultyBand": "BEGINNER",
    "uniquenessStatus": "UNIQUE",
    "fingerprint": "a4f8...SHA256"
  }
}
```

---

## 3. Normalization Rules

1. **Required Cells:**
   Serialized in row-major order: sorted by `row` ascending, then `column` ascending.

2. **Checkpoints:**
   Serialized in strictly ascending checkpoint number order: 1, 2, ..., $K$.

3. **Blocked Edges:**
   Endpoints of every wall are normalized canonically such that `first <= second` in row-major comparison. The list of blocked edges is sorted by `first`, then `second`.

4. **Zero Solutions in Playable Assets:**
   To guarantee security and cheat resistance, the solved Hamiltonian path is NOT stored within user-facing puzzle assets or level definitions. Solvability is verified prior to asset admission.

---

## 4. Canonical Fingerprint

Every puzzle definition produces an order-independent canonical representation:
`DIM:{rows}x{cols}|CELLS:{r1},{c1};{r2},{c2}|CP:{num1}@{r},{c};{num2}@{r},{c}|WALLS:{r1},{c1}-{r2},{c2};...`

The SHA-256 digest of this canonical string serves as the immutable `fingerprint` property verified by `CatalogIntegrityChecker`.
