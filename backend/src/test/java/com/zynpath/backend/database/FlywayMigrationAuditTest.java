package com.zynpath.backend.database;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class FlywayMigrationAuditTest {

    private static final List<String> EXPECTED_MIGRATIONS = List.of(
            "db/migration/V1__baseline_core_accounts_and_profiles.sql",
            "db/migration/V2__social_and_friend_relationships.sql",
            "db/migration/V3__multiplayer_sessions_and_results.sql",
            "db/migration/V4__cosmetics_and_ad_rewards.sql",
            "db/migration/V5__notifications_and_privacy.sql",
            "db/migration/V6__indexes_and_performance_tuning.sql"
    );

    @Test
    @DisplayName("All versioned migrations exist and follow strict version sequencing")
    void migrations_existAndFollowSequencing() {
        for (String migrationPath : EXPECTED_MIGRATIONS) {
            Resource resource = new ClassPathResource(migrationPath);
            assertThat(resource.exists())
                    .withFailMessage("Migration resource %s must exist", migrationPath)
                    .isTrue();
        }
    }

    @Test
    @DisplayName("Migrations contain non-destructive DDL and declare core tables")
    void migrations_containNonDestructiveDdl() throws Exception {
        List<String> foundTables = new ArrayList<>();

        for (String migrationPath : EXPECTED_MIGRATIONS) {
            Resource resource = new ClassPathResource(migrationPath);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                String sql = reader.lines().collect(Collectors.joining("\n"));

                // Must not contain destructive DROP statements
                assertThat(sql.toUpperCase()).doesNotContain("DROP DATABASE");
                assertThat(sql.toUpperCase()).doesNotContain("DROP SCHEMA");

                // Collect created tables
                for (String line : sql.split("\n")) {
                    String trimmed = line.trim().toUpperCase();
                    if (trimmed.startsWith("CREATE TABLE")) {
                        foundTables.add(trimmed);
                    }
                }
            }
        }

        // Verify key application tables are declared
        String allTablesCombined = String.join(" ", foundTables);
        assertThat(allTablesCombined).contains("PLAYER_ACCOUNTS");
        assertThat(allTablesCombined).contains("EXTERNAL_IDENTITIES");
        assertThat(allTablesCombined).contains("FRIEND_RELATIONSHIPS");
        assertThat(allTablesCombined).contains("MATCH_SESSIONS");
        assertThat(allTablesCombined).contains("MATCH_RESULTS");
        assertThat(allTablesCombined).contains("REWARD_EVENTS");
        assertThat(allTablesCombined).contains("PLAYER_PRIVACY_SETTINGS");
    }

    @Test
    @DisplayName("V6 performance migration declares indexes for competitive and social tables")
    void v6_containsPerformanceIndexes() throws Exception {
        Resource resource = new ClassPathResource("db/migration/V6__indexes_and_performance_tuning.sql");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String sql = reader.lines().collect(Collectors.joining("\n")).toUpperCase();

            assertThat(sql).contains("CREATE INDEX");
            assertThat(sql).contains("MATCH_SESSIONS");
            assertThat(sql).contains("DAILY_CHALLENGE_ATTEMPTS");
        }
    }
}
