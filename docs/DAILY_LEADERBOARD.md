# Zynpath: Daily Leaderboard Specification

## 1. Scope & Partitioning
- The Daily Leaderboard is strictly scoped to **one canonical UTC challenge date**.
- Results from different dates or different puzzle revisions are NEVER merged into the same ranking table.
- Only verified, server-authoritative completions from official online attempts (`DailyVerificationStatus.SERVER_VALIDATED` or `LEADERBOARD_ELIGIBLE`) are eligible for ranking.

## 2. Ranking Algorithm & Deterministic Tie-Breaking
- **Primary Metric**: Authoritative elapsed solve time (`solveTimeMs` in ascending order).
- **Secondary Determinism (Equal Times)**:
  - When two or more players achieve the identical solve time down to millisecond precision, Zynpath applies **Standard Competition Ranking** ("1224" ranking):
    - Example:
      - Player A: 45,210 ms -> Rank 1
      - Player B: 47,000 ms -> Rank 2
      - Player C: 47,000 ms -> Rank 2
      - Player D: 49,150 ms -> Rank 4
  - Ties are acknowledged honestly. The system never randomly invents a winner or breaks ties using internal account creation timestamps or arbitrary hashes.

## 3. Bounded Pagination & Query Efficiency
- The client requests pages via `GET /api/daily/leaderboard/{dateKey}?page={page}&pageSize={pageSize}`.
- Max page size is capped at 100 entries (default 50).
- Responses include:
  - `challengeId` & `dateKey`
  - `puzzleFingerprint`
  - `entries`: Ordered list of `DailyLeaderboardEntry`
  - `playerEntry`: Authenticated player's personal standing and rank (even if outside the current page)
  - `totalEntries`: Total number of ranked participants
  - `page` & `pageSize`

## 4. Privacy & Public Profile Exposure
Public leaderboard responses strictly enforce the player privacy boundary:
- **Exposed fields**:
  - `publicZynpathId` (e.g. `ZYN-789012`)
  - `displayName` (e.g. "QuantumSolver")
  - `avatarId` (e.g. "avatar_geometric_teal")
  - `solveTimeMs` (e.g. `45210`)
  - `rank` (e.g. `1`)
  - `verificationStatus` (`SERVER_VALIDATED` or `LEADERBOARD_ELIGIBLE`)
- **Prohibited fields** (NEVER included in responses):
  - Email addresses
  - OAuth/Google provider subject IDs
  - Authentication JWTs or refresh tokens
  - Internal database UUIDs

## 5. Fair Play & Authenticity Guarantee
- **Zero Bot Entries**: No synthetic or fake players are ever injected into the production daily leaderboard.
- **Empty State**: If no players have completed the daily challenge yet, the UI displays an authentic empty state ("Be the first to claim the top spot!").
