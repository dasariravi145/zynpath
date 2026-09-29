# Zynpath Server-Authoritative Leaderboards

## 1. Architectural Foundation

The Zynpath leaderboard system is strictly server-authoritative. Rankings originate exclusively from verified, finalized match results stored in the backend service.

- **No Synthetic Rankings**: The leaderboard is never seeded with fake, simulated, or placeholder players. If a category or period has zero active entries, an empty state is displayed honestly.
- **No Client Ranking Authority**: Android does not compute global rankings or invent player positions.
- **Privacy by Design**: Leaderboard responses expose only public player identifiers (`publicZynpathId`, `displayName`, `avatarId`, `metricValue`, `rank`). Internal player UUIDs, emails, auth provider tokens, and guest IDs are strictly excluded.

---

## 2. Leaderboard Categories

Three foundational categories are activated based on unambiguous metrics:

1. **Quick Duel Victories (`QUICK_DUEL_WINS`)**
   - Metric: Number of server-validated Quick Duel 1v1 wins.
   - Formatted: `"N Wins"`.
2. **Mini League 1st Places (`MINI_LEAGUE_WINS`)**
   - Metric: Number of first-place finishes in 2–5 player Mini Leagues.
   - Formatted: `"N 1st Places"`.
3. **Total Completions (`TOTAL_COMPLETIONS`)**
   - Metric: Number of valid, verified puzzle solutions submitted across all multiplayer modes.
   - Formatted: `"N Solves"`.

---

## 3. Time Periods

Every category supports three time windows:
- **All Time (`ALL_TIME`)**: Cumulative lifetime verified metrics.
- **This Month (`THIS_MONTH`)**: Current UTC calendar month.
- **This Week (`THIS_WEEK`)**: Current UTC calendar week (Monday through Sunday).

---

## 4. Deterministic Tie Handling

When two or more players have equal metric values:
- Players share the same competition rank (e.g. standard ranking: 1st, 2nd, 2nd, 4th).
- Secondary ordering in data arrays uses deterministic ordering (`publicZynpathId ASC`) to ensure stable pagination between pages.

---

## 5. API Endpoints

### List Categories
`GET /api/v1/multiplayer/leaderboard/categories`
Returns available categories with titles and descriptions.

### Query Leaderboard
`GET /api/v1/multiplayer/leaderboard?category=QUICK_DUEL_WINS&period=ALL_TIME&page=0&pageSize=20`
Headers: `Authorization: Bearer <token>` (Optional; guests can view leaderboards anonymously).

#### Response Structure:
```json
{
  "category": "QUICK_DUEL_WINS",
  "period": "ALL_TIME",
  "entries": [
    {
      "rank": 1,
      "publicZynpathId": "ZYN-8K4F-92XA",
      "displayName": "PathMaster",
      "avatarId": "avatar_compass",
      "metricValue": 42,
      "formattedValue": "42 Wins"
    }
  ],
  "myRank": 1,
  "myMetricValue": 42,
  "page": 0,
  "pageSize": 20,
  "totalEntries": 1,
  "hasMore": false
}
```
If the requesting player is not ranked or has zero eligible completions, `myRank` is `null`.

---

## 6. Daily Challenge Leaderboard (Prompt 25)

The Daily Leaderboard provides official date-scoped competitive rankings for each canonical daily challenge:
- **Endpoint**: `GET /api/daily/leaderboard/{dateKey}?page={page}&pageSize={pageSize}`
- **Metric**: Authoritative elapsed solve time (`solveTimeMs` ascending).
- **Tie Handling**: Standard Competition Ranking ("1224" ranking) where identical millisecond solve times share rank without arbitrary winner invention.
- **Privacy Enforcement**: Public profiles only (`publicZynpathId`, `displayName`, `avatarId`, `solveTimeMs`, `rank`, `verificationStatus`).
- **Zero Bots**: Empty boards render authentic empty states without synthetic AI records.
- **Full Specification**: Refer to [`docs/DAILY_LEADERBOARD.md`](file:///d:/Zynpath/docs/DAILY_LEADERBOARD.md).

---

## 7. Security Hardening & Leaderboard Integrity (Prompt 36)

- **Closed Ingestion Pipeline**: There is zero API surface for client-submitted leaderboard ranks, scores, or victories. Leaderboard entries are derived strictly from server-validated `MatchFinalization` and `DailyChallengeVerification` domain events.
- **Cheating & Exploitation Resistance**: Only solutions validated by `ServerPuzzleValidator` (orthogonal moves, 100% cell coverage, ascending checkpoints, zero wall crossings) can qualify for competitive leaderboards.
- **Bounded Pagination Limits**: Leaderboard page queries strictly enforce a maximum page size of 100 records (`pageSize = Math.min(pageSize, 100)`), preventing database denial-of-service and bulk scraping.
- **Public Read Rate Limiting**: Governed by `RateLimitPolicy.PUBLIC_READ` (60 req/min for anonymous guests) and `RateLimitPolicy.DEFAULT_AUTHENTICATED` (120 req/min for authenticated players).

---

## 8. Achievement Gating & Fair Engagement (Prompt 41)
- **Authoritative Ranking Requirement**: The `daily_leaderboard_ranked` achievement ("On the Board") is strictly awarded only after the backend confirms `isLeaderboardEligible == true` on the official daily challenge verification response.
- **Client Integrity**: Android never locally fabricates or unlocks leaderboard-dependent achievements based on client-side solve times or unverified attempts.

