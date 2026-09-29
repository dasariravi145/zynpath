# Premium Content Delivery & Security Architecture

## Overview

Zynpath provides a secure, entitlement-aware content delivery pipeline for Premium Solo puzzle packs. Content delivery is server-authoritative, ensuring that unentitled clients cannot acquire or decrypt full pack puzzle collections while permitting entitled users to download, verify, and store content for offline access.

---

## Content Endpoints

### 1. Catalog Query
- **`GET /api/v1/content/packs`**
- **Authorization**: Optional Bearer session token.
- **Response**: Array of `PremiumPackManifestDto` objects.
- **Description**: Delivers metadata for all published and upcoming packs. Does not include raw puzzle boards or solver paths.

### 2. Pack Manifest
- **`GET /api/v1/content/packs/{packId}/manifest`**
- **Authorization**: Optional Bearer session token.
- **Response**: Single `PremiumPackManifestDto`.
- **Status Codes**:
  - `200 OK`: Manifest retrieved.
  - `404 Not Found`: Unknown pack ID.

### 3. Entitlement-Gated Pack Download
- **`GET /api/v1/content/packs/{packId}/download`**
- **Authorization**: **Required** Bearer session token (`Authorization: Bearer <sessionToken>`).
- **Enforcement**:
  1. Validates caller's session token via `SessionSecurityService`.
  2. Queries authoritative entitlement state from `SubscriptionService.getEntitlementForAccount(accountId)`.
  3. Checks whether the entitlement status is `ACTIVE` or `CANCELLED_BUT_ACTIVE`.
- **Status Codes**:
  - `200 OK`: Returns `PremiumPackDownloadResponse` containing the full manifest and puzzle definitions.
  - `401 Unauthorized`: Missing or invalid session token.
  - `403 Forbidden`: Account does not possess an active `PREMIUM_SOLO_PACKS` entitlement.
  - `404 Not Found`: Pack ID not found or pack is unreleased (`COMING_SOON`).

---

## Client Validation Pipeline & Atomic Activation

Upon downloading a pack payload from the server, the Android client executes a mandatory multi-stage validation pipeline (`PremiumPackStorageManager`) before marking the collection playable:

1. **Manifest Consistency**: Confirms `manifest.puzzleCount == puzzles.size`.
2. **Structural Validation**: Every puzzle must satisfy `PuzzleDefinitionValidator` (coordinate bounds, start checkpoint #1, ascending checkpoints, valid wall edges).
3. **Fingerprint Verification**: Each puzzle's SHA-256 fingerprint is computed independently and matched against `manifest.puzzles[i].fingerprint`.
4. **Solver Solvability Check**: Each puzzle is submitted to the local Hamiltonian `PuzzleSolver` to mathematically prove at least one valid complete path exists.
5. **Atomic Directory Swap**:
   - Files are written to a temporary staging folder (`<packId>_staging_<timestamp>`).
   - Once all checks pass, the staging folder is atomically renamed/copied to `<packId>`.
   - If validation fails at any stage, the temporary staging folder is deleted and the previous installed version is retained intact.

---

## Content Security Principles

- **No Complete Solutions in Metadata**: Puzzle definitions specify only start, numbered checkpoints, grid dimensions, and blocked edges. Solutions are never included in manifests or client downloads.
- **Zero Client Trust**: Possession of a pack ID does not grant download rights. The server validates entitlement authoritatively.
- **No Leaked Secrets**: Manifests and download responses contain no API keys, billing tokens, or cloud credentials.
