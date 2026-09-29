# Database Migration & Schema Evolution Strategy

## 1. Migration Tool & Architectural Decision
Zynpath employs **Flyway** for ordered, version-controlled database migrations against PostgreSQL. 
- **Tooling:** Flyway Core (`flyway-core`) integrated via Spring Boot.
- **Location:** `backend/src/main/resources/db/migration/`
- **Strict Production Schema Policy:** `spring.jpa.hibernate.ddl-auto: validate` is strictly enforced in production (`application-prod.yml`). Hibernate is NEVER permitted to automatically alter (`update`) or drop (`create-drop`) production tables.

---

## 2. Versioned Migration Catalog

The schema evolution is organized into granular, sequential migration scripts:

| Version | File | Responsibility & Managed Tables |
|---|---|---|
| **V1** | [V1__baseline_core_accounts_and_profiles.sql](file:///d:/Zynpath/backend/src/main/resources/db/migration/V1__baseline_core_accounts_and_profiles.sql) | Baseline: `player_accounts`, `external_identities`, `player_profiles`, `subscription_entitlements`, `daily_challenge_attempts`, `competitive_stats`. |
| **V2** | [V2__social_and_friend_relationships.sql](file:///d:/Zynpath/backend/src/main/resources/db/migration/V2__social_and_friend_relationships.sql) | Social Graph: `friend_relationships` (enforcing canonical order `player_id_1 < player_id_2`), `friend_requests`, `player_blocks`, `multiplayer_invitations`. |
| **V3** | [V3__multiplayer_sessions_and_results.sql](file:///d:/Zynpath/backend/src/main/resources/db/migration/V3__multiplayer_sessions_and_results.sql) | Multiplayer: `match_sessions`, `match_participants`, immutable `match_results`. |
| **V4** | [V4__cosmetics_and_ad_rewards.sql](file:///d:/Zynpath/backend/src/main/resources/db/migration/V4__cosmetics_and_ad_rewards.sql) | Monetization: `player_cosmetics`, `player_hint_balances`, `reward_events`, `admob_ssv_records`. |
| **V5** | [V5__notifications_and_privacy.sql](file:///d:/Zynpath/backend/src/main/resources/db/migration/V5__notifications_and_privacy.sql) | GDPR/Compliance: `player_notifications`, `notification_preferences`, `device_push_registrations`, `notification_delivery_events`, `player_privacy_settings`, `account_export_requests`, `account_deletion_audits`. |
| **V6** | [V6__indexes_and_performance_tuning.sql](file:///d:/Zynpath/backend/src/main/resources/db/migration/V6__indexes_and_performance_tuning.sql) | Optimization: Partial indexes for active invitations, daily challenge composite lookups, match participant lookups, and session tokens. |

---

## 3. Data Preservation Invariants
All migrations MUST preserve existing operational data:
1. **Player Accounts & Guest Linking:** Foreign keys between `external_identities` and `player_accounts` utilize `ON DELETE CASCADE` only upon deliberate account deletion. Guest-to-Google/Facebook linking preserves the canonical `player_id`.
2. **Deterministic Daily Results:** `daily_challenge_attempts` enforces `UNIQUE(player_id, challenge_date)`. A player's verified score can never be overwritten by a subsequent or slower attempt.
3. **Canonical Social Relationships:** `friend_relationships` enforces check constraint `player_id_1 < player_id_2` to guarantee zero duplicated or reciprocal relation rows.
4. **Authoritative Match Results:** `match_results` rows are append-only and immutable. Once written and signed by the server authority, rows cannot be mutated.
5. **Subscription Entitlements:** Store Google Play purchase tokens with unique constraints to prevent double-crediting across accounts.

---

## 4. Migration Execution & Baseline Policy
In environments with pre-existing legacy tables:
- `spring.flyway.baseline-on-migrate: true`
- `spring.flyway.baseline-version: 0`
- `spring.flyway.validate-on-migrate: true`

When Flyway runs against an existing database, it validates checksums against `flyway_schema_history`. If an unrecorded schema exists, it marks version `0` as baseline and applies `V1`–`V6` sequentially.

---

## 5. Migration Failure & Safe Recovery
- **Transactional DDL:** PostgreSQL natively supports transactional DDL. If a syntax error or constraint violation occurs during a migration, PostgreSQL automatically rolls back the entire migration transaction.
- **Fail-Fast Boot:** If a migration fails, Flyway records `success = FALSE` in `flyway_schema_history`, and the Spring Boot application fails to start.
- **Resolution Procedure:**
  1. Inspect startup log for specific PostgreSQL error code.
  2. Never manually drop tables in production.
  3. Execute `flyway repair` (via CLI or maintenance container) after rectifying the offending SQL script, or restore the pre-migration snapshot taken immediately prior to release.

---

## 6. Database Migration Integration Testing (Prompt 47)

### 6.1 Test Execution Summary (`FlywayMigrationAuditTest`)
- **Suite**: `com.zynpath.backend.migration.FlywayMigrationAuditTest`
- **Total Tests**: 5 / 5 PASSED (100% Pass Rate).
- **Verified Behaviors**:
  1. `testMigrationFilesExistAndOrdered`: Verifies all versioned migrations `V1` through `V6` are present in `src/main/resources/db/migration` with continuous strict ascending version numbers.
  2. `testMigrationScriptIntegrity_containsExpectedTables`: Validates that essential relational tables (`player_accounts`, `player_profiles`, `friend_relationships`, `match_sessions`, `match_results`, `subscription_entitlements`, `admob_ssv_records`) are declared across the scripts.
  3. `testIndexesDeclared_forPerformance`: Audits `V6__performance_indexes.sql` ensuring compound and partial index coverage for active matchmaking queues and leaderboard rankings.
  4. `testCheckConstraintsAndImmutability`: Confirms relational integrity rules (`player_id_1 < player_id_2`, non-null foreign keys) are enforced.
  5. `testNoDestructiveDropStatements`: Verifies no migration script executes destructive `DROP TABLE`, `DROP DATABASE`, or `TRUNCATE` operations.

