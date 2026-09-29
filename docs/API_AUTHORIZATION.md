# Zynpath API Authorization and Endpoint Inventory

## 1. Authorization Access Tiers
Zynpath categorizes all backend endpoints into strict access tiers defined in `com.zynpath.backend.security.model.EndpointAccessTier`:

1. **PUBLIC**: Accessible without an active session token (e.g., auth exchange, public catalogs, daily puzzle definition, server health check).
2. **AUTHENTICATED**: Requires a valid, non-expired `Bearer` token issued by `SessionSecurityService`.
3. **OWNER_ONLY**: Requires the authenticated player to match the resource owner identity (e.g., personal analytics, notifications, account export, account deletion).
4. **FRIEND_RELATIONSHIP_REQUIRED**: Requires an established mutual friendship or allowed invitation relationship.
5. **MATCH_PARTICIPANT_ONLY**: Restricted to players actively matched in the referenced competitive duel session.
6. **ROOM_PARTICIPANT_ONLY**: Restricted to host or members currently present in the referenced Mini League room.
7. **ADMIN_ONLY**: Reserved for administrative operations; denied to all ordinary player sessions.

---

## 2. Complete Endpoint Inventory

### Authentication & Account Management (`/api/v1/auth`, `/api/v1/account`)
| Method | Path | Access Tier | Rate Limit Policy | Authorization & Security Notes |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/exchange` | `PUBLIC` | `AUTH` (10/min) | Verifies Google/Facebook identity token; issues `zyn_` session token. |
| `POST` | `/api/v1/auth/link` | `PUBLIC` | `AUTH` (10/min) | Links verified provider identity to guest account without progress loss. |
| `GET` | `/api/v1/auth/me` | `AUTHENTICATED` | `DEFAULT_API` (120/min) | Returns authenticated player profile; resolves principal from session. |
| `POST` | `/api/v1/auth/signout` | `AUTHENTICATED` | `DEFAULT_API` (120/min) | Server-side revocation of session token. |
| `GET` | `/api/v1/account/me` | `OWNER_ONLY` | `DEFAULT_API` (120/min) | Returns private account details for the session owner. |
| `GET` | `/api/v1/account/export` | `OWNER_ONLY` | `SENSITIVE` (5/hr) | Exports all personal player data for the authenticated account only. |
| `DELETE` | `/api/v1/account/me` | `OWNER_ONLY` | `SENSITIVE` (5/hr) | Enforces double-confirmation and revocations; isolates deleted state. |

### Social & Presence (`/api/v1/social`)
| Method | Path | Access Tier | Rate Limit Policy | Authorization & Security Notes |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/social/me` | `AUTHENTICATED` | `DEFAULT_API` (120/min) | Retrieves calling player's own public identity profile. |
| `GET` | `/api/v1/social/players/search` | `AUTHENTICATED` | `DEFAULT_API` (120/min) | Exact-ID search only; prevents bulk account scraping and enumeration. |
| `GET` | `/api/v1/social/friends` | `AUTHENTICATED` | `DEFAULT_API` (120/min) | Lists mutual friends for the authenticated player. |
| `GET` | `/api/v1/social/friends/requests/incoming` | `AUTHENTICATED` | `DEFAULT_API` (120/min) | Lists incoming friend requests for the authenticated player. |
| `GET` | `/api/v1/social/friends/requests/outgoing` | `AUTHENTICATED` | `DEFAULT_API` (120/min) | Lists pending outgoing requests sent by the authenticated player. |
| `POST` | `/api/v1/social/friends/requests` | `AUTHENTICATED` | `INVITATIONS_AND_ROOMS` (30/min) | Anti-abuse checks: max 50 pending, blocks, self-request prevention. |
| `POST` | `/api/v1/social/friends/requests/{id}/accept` | `AUTHENTICATED` | `DEFAULT_API` (120/min) | Enforces recipient authorization before establishing mutual friendship. |
| `POST` | `/api/v1/social/friends/requests/{id}/reject` | `AUTHENTICATED` | `DEFAULT_API` (120/min) | Enforces recipient authorization before rejecting pending request. |
| `POST` | `/api/v1/social/friends/requests/{id}/cancel` | `AUTHENTICATED` | `DEFAULT_API` (120/min) | Enforces sender authorization before cancelling pending request. |
| `DELETE` | `/api/v1/social/friends/{friendId}` | `AUTHENTICATED` | `DEFAULT_API` (120/min) | Removes mutual friendship transactionally. |
| `GET` | `/api/v1/social/blocks` | `AUTHENTICATED` | `DEFAULT_API` (120/min) | Lists players blocked by the authenticated caller. |
| `POST` | `/api/v1/social/blocks/{targetId}` | `AUTHENTICATED` | `DEFAULT_API` (120/min) | Directional block preventing requests, invites, or presence visibility. |
| `DELETE` | `/api/v1/social/blocks/{targetId}` | `AUTHENTICATED` | `DEFAULT_API` (120/min) | Removes block relationship. |
| `POST` | `/api/v1/social/presence/heartbeat` | `AUTHENTICATED` | `DEFAULT_API` (120/min) | Ephemeral presence update bounded to authenticated principal. |
| `GET` | `/api/v1/social/presence` | `AUTHENTICATED` | `DEFAULT_API` (120/min) | Resolves online presence only for confirmed mutual friends. |
| `POST` | `/api/v1/social/invitations/multiplayer` | `AUTHENTICATED` | `INVITATIONS_AND_ROOMS` (30/min) | Verifies friend status and non-blocked status before creating invite. |

### Competitive Multiplayer (`/api/v1/multiplayer`)
| Method | Path | Access Tier | Rate Limit Policy | Authorization & Security Notes |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/multiplayer/matchmaking/quick-duel` | `AUTHENTICATED` | `MATCHMAKING` (20/min) | Enforces valid ELO range and server-issued authoritative puzzle. |
| `POST` | `/api/v1/multiplayer/matches/duel/invite` | `AUTHENTICATED` | `INVITATIONS_AND_ROOMS` (30/min) | Friend duel invitation; checks mutual friendship. |
| `POST` | `/api/v1/multiplayer/matches/duel/accept` | `AUTHENTICATED` | `INVITATIONS_AND_ROOMS` (30/min) | Target player verification before match room creation. |
| `POST` | `/api/v1/multiplayer/matches/duel/decline` | `AUTHENTICATED` | `INVITATIONS_AND_ROOMS` (30/min) | Target player verification before invitation cancellation. |
| `GET` | `/api/v1/multiplayer/matches/{matchId}` | `MATCH_PARTICIPANT_ONLY` | `DEFAULT_API` (120/min) | Enforces player participation in match before revealing state. |
| `POST` | `/api/v1/multiplayer/matches/{matchId}/claim` | `MATCH_PARTICIPANT_ONLY` | `SUBMISSIONS` (30/min) | Full server path verification, wall validation, timing, idempotency. |
| `POST` | `/api/v1/multiplayer/matches/{matchId}/forfeit` | `MATCH_PARTICIPANT_ONLY` | `DEFAULT_API` (120/min) | Authorized participant concession. |
| `POST` | `/api/v1/multiplayer/leagues/rooms` | `AUTHENTICATED` | `INVITATIONS_AND_ROOMS` (30/min) | Room creation (2-5 player cap); creator set as host. |
| `POST` | `/api/v1/multiplayer/leagues/rooms/{id}/join` | `AUTHENTICATED` | `INVITATIONS_AND_ROOMS` (30/min) | Capacity and room state check; prevents joining active matches. |
| `POST` | `/api/v1/multiplayer/leagues/rooms/{id}/start` | `ROOM_PARTICIPANT_ONLY` | `INVITATIONS_AND_ROOMS` (30/min) | Host-only authorization to transition room to countdown. |
| `GET` | `/api/v1/multiplayer/leagues/rooms/{id}` | `ROOM_PARTICIPANT_ONLY` | `DEFAULT_API` (120/min) | Member-only view of private league standings and participants. |
| `GET` | `/api/v1/multiplayer/leaderboard` | `PUBLIC` | `DEFAULT_API` (120/min) | Bounded pagination (max 100); only server-verified results shown. |

### Daily Challenge (`/api/v1/daily`)
| Method | Path | Access Tier | Rate Limit Policy | Authorization & Security Notes |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/daily/challenge` | `PUBLIC` | `DEFAULT_API` (120/min) | Returns canonical daily challenge puzzle (available offline/guests). |
| `POST` | `/api/v1/daily/attempt/start` | `AUTHENTICATED` | `SUBMISSIONS` (30/min) | Records official server attempt timestamp for competitive timing. |
| `GET` | `/api/v1/daily/attempt/active` | `AUTHENTICATED` | `DEFAULT_API` (120/min) | Resumes active daily attempt for reconnection. |
| `POST` | `/api/v1/daily/attempt/submit` | `AUTHENTICATED` | `SUBMISSIONS` (30/min) | Server path validation, wall checks, authoritative time ranking. |
| `GET` | `/api/v1/daily/result` | `AUTHENTICATED` | `DEFAULT_API` (120/min) | Returns authenticated player's personal daily result. |
| `GET` | `/api/v1/daily/leaderboard` | `PUBLIC` | `DEFAULT_API` (120/min) | Bounded pagination (max 100); only verified attempts ranked. |
| `POST` | `/api/v1/daily/sync-provisional`| `AUTHENTICATED` | `SUBMISSIONS` (30/min) | Synchronizes offline completion without polluting competitive board. |

### Subscriptions & In-App Purchases (`/api/v1/subscription`)
| Method | Path | Access Tier | Rate Limit Policy | Authorization & Security Notes |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/subscription/entitlement` | `PUBLIC` | `DEFAULT_API` (120/min) | Queries authoritative entitlement status (returns free tier for anon). |
| `POST` | `/api/v1/subscription/verify` | `AUTHENTICATED` | `SENSITIVE` (5/hr) | `BillingIntegrityGuard` ensures purchase token uniqueness across accounts. |
| `POST` | `/api/v1/subscription/refresh` | `AUTHENTICATED` | `DEFAULT_API` (120/min) | Refreshes entitlement state with Google Play backend. |
| `POST` | `/api/v1/subscription/restore` | `AUTHENTICATED` | `SENSITIVE` (5/hr) | Restores purchases linked to the authenticated player account. |

### Rewarded Ads (`/api/v1/ads`)
| Method | Path | Access Tier | Rate Limit Policy | Authorization & Security Notes |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/ads/reward/verify` | `AUTHENTICATED` | `SUBMISSIONS` (30/min) | `BillingIntegrityGuard` deduplicates reward events; anti-replay. |
| `GET` | `/api/v1/ads/reward/balance` | `PUBLIC` | `DEFAULT_API` (120/min) | Returns authenticated or guest hint balance. |
| `GET` | `/api/v1/ads/ssv-callback` | `PUBLIC` | `SUBMISSIONS` (30/min) | AdMob Server-Side Verification webhook; signature-verified. |

### Content & Packs (`/api/v1/content/packs`)
| Method | Path | Access Tier | Rate Limit Policy | Authorization & Security Notes |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/content/packs` | `PUBLIC` | `DEFAULT_API` (120/min) | Catalog of available and upcoming Premium packs. |
| `GET` | `/api/v1/content/packs/{id}/manifest` | `PUBLIC` | `DEFAULT_API` (120/min) | Manifest describing pack details, sizes, and puzzle count. |
| `GET` | `/api/v1/content/packs/{id}/download` | `AUTHENTICATED` | `SENSITIVE` (5/hr) | Server verifies active Premium subscription before download release. |

### Cosmetics & Personalization (`/api/v1/cosmetics`)
| Method | Path | Access Tier | Rate Limit Policy | Authorization & Security Notes |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/cosmetics/catalog` | `PUBLIC` | `DEFAULT_API` (120/min) | Public cosmetic catalog and version metadata. |
| `GET` | `/api/v1/cosmetics/equipped` | `PUBLIC` | `DEFAULT_API` (120/min) | Returns equipped cosmetics for player (defaults for anonymous). |
| `POST` | `/api/v1/cosmetics/equipped` | `AUTHENTICATED` | `DEFAULT_API` (120/min) | Verifies item ownership / entitlement before equipping. |
| `GET` | `/api/v1/cosmetics/public/{id}` | `PUBLIC` | `DEFAULT_API` (120/min) | Shows equipped cosmetics for public profile display only. |

### Offline Sync & Analytics (`/api/v1/sync`, `/api/v1/analytics`, `/api/v1/health`)
| Method | Path | Access Tier | Rate Limit Policy | Authorization & Security Notes |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/sync/batch` | `PUBLIC` | `SUBMISSIONS` (30/min) | Batched idempotent progress sync; supports offline guest and account sync. |
| `GET` | `/api/v1/sync/progress` | `PUBLIC` | `DEFAULT_API` (120/min) | Retrieves progress for local reconciliation. |
| `GET` | `/api/v1/analytics/personal` | `OWNER_ONLY` | `DEFAULT_API` (120/min) | Personal game stats and progression; strictly owner-accessible. |
| `GET` | `/api/v1/health` | `PUBLIC` | `DEFAULT_API` (120/min) | Health and liveness probe for monitoring and load balancers. |
