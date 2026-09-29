# Backend Request Optimization & Caching

## 1. High-Frequency Endpoints
Zynpath's Spring Boot backend serves requests across authentication, solo synchronization, daily challenges, and real-time multiplayer duels.

### Key Optimizations:
1. **Competitive Leaderboards Cache**:
   - `CompetitiveService.getLeaderboard` previously iterated across all finalized sessions and performed multiple `playerAccountService.findById` lookups per query.
   - Replaced with an in-memory `leaderboardCache` (`Map<String, List<CachedRankedEntry>>`).
   - Leaderboard computations are cached until a new match concludes via `recordFinalizedMatch()`, which safely clears the cache.
   - Sliced pagination (`subList(fromIndex, toIndex)`) executes in O(1) time.
2. **Daily Challenge Ranking Cache**:
   - `DailyChallengeService.getDailyLeaderboard` and `calculatePlayerRank` share `getSortedEligibleResults(dateKey)`.
   - Results are filtered and sorted once per date, cached in `sortedEligibleResultsByDate`, and invalidated only when a new completion is finalized or verified.
3. **Idempotent Match Finalization**:
   - Session completion checks against `finalizedMatchIds` set before performing database writes or ranking calculations.
4. **Bounded Pagination & Payloads**:
   - Page sizes are strictly capped (`max = 50`) to prevent heavy payloads.
   - Public leaderboard endpoints return minimal public identity DTOs (`publicZynpathId`, `displayName`, `avatarId`), avoiding private account data leakage.
