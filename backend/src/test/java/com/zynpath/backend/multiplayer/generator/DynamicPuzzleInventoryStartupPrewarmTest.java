package com.zynpath.backend.multiplayer.generator;

import com.zynpath.backend.multiplayer.model.GameMode;
import com.zynpath.backend.multiplayer.puzzle.MultiplayerPuzzlePool;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Focused verification tests for configuration-controlled startup puzzle inventory prewarming.
 *
 * Implements Step 17.31A:
 * - Test A: Disabled by default (fail-safe).
 * - Test B: Explicitly disabled.
 * - Test C: Explicitly enabled (deterministic asynchronous execution).
 * - Test D: Production YAML configuration audit.
 */
class DynamicPuzzleInventoryStartupPrewarmTest {

    @Test
    @DisplayName("Test A — Disabled by default: no tasks scheduled, no persistence port calls")
    void testA_disabledByDefault() {
        ProceduralPuzzleGenerator generator = mock(ProceduralPuzzleGenerator.class);
        MultiplayerPuzzlePersistencePort persistencePort = mock(MultiplayerPuzzlePersistencePort.class);
        MultiplayerPuzzlePool fallbackPool = mock(MultiplayerPuzzlePool.class);

        DynamicPuzzleInventory inventory = new DynamicPuzzleInventory(generator, persistencePort, fallbackPool);

        assertThat(inventory.isStartupPrewarmEnabled()).isFalse();

        inventory.init();

        // Must not call countAvailablePuzzles, saveGeneratedPuzzle, or any persistence method
        verifyNoInteractions(persistencePort);
        verifyNoInteractions(generator);
    }

    @Test
    @DisplayName("Test B — Explicitly disabled: no startup work or persistence calls occur")
    void testB_explicitlyDisabled() {
        ProceduralPuzzleGenerator generator = mock(ProceduralPuzzleGenerator.class);
        MultiplayerPuzzlePersistencePort persistencePort = mock(MultiplayerPuzzlePersistencePort.class);
        MultiplayerPuzzlePool fallbackPool = mock(MultiplayerPuzzlePool.class);

        DynamicPuzzleInventory inventory = new DynamicPuzzleInventory(generator, persistencePort, fallbackPool, false);

        assertThat(inventory.isStartupPrewarmEnabled()).isFalse();

        inventory.init();

        verifyNoInteractions(persistencePort);
        verifyNoInteractions(generator);
    }

    @Test
    @DisplayName("Test C — Explicitly enabled: follows prewarming execution path deterministically")
    void testC_explicitlyEnabled() throws Exception {
        ProceduralPuzzleGenerator generator = mock(ProceduralPuzzleGenerator.class);
        MultiplayerPuzzlePersistencePort persistencePort = mock(MultiplayerPuzzlePersistencePort.class);
        MultiplayerPuzzlePool fallbackPool = mock(MultiplayerPuzzlePool.class);

        // Stub countAvailablePuzzles to 3 so buffer is already sufficient (avoids expensive generation)
        when(persistencePort.countAvailablePuzzles(any(GameMode.class), any(CompetitiveDifficultyTier.class))).thenReturn(3L);

        ExecutorService testExecutor = Executors.newSingleThreadExecutor();
        CountDownLatch latch = new CountDownLatch(1);

        try {
            DynamicPuzzleInventory inventory = new DynamicPuzzleInventory(
                    generator, persistencePort, fallbackPool, true, testExecutor
            );

            assertThat(inventory.isStartupPrewarmEnabled()).isTrue();

            inventory.init();

            // Enqueue sentinel task to ensure FIFO completion of the submitted startup prewarm task
            testExecutor.submit(latch::countDown);

            boolean finished = latch.await(5, TimeUnit.SECONDS);
            assertThat(finished).isTrue();

            // Verify countAvailablePuzzles was called for each mode and tier (2 modes * 3 tiers = 6 calls)
            verify(persistencePort, times(6)).countAvailablePuzzles(any(GameMode.class), any(CompetitiveDifficultyTier.class));

            // Verify no generation or saving occurred because count was already 3
            verify(persistencePort, never()).saveGeneratedPuzzle(any());
            verifyNoInteractions(generator);
        } finally {
            testExecutor.shutdownNow();
        }
    }

    @Test
    @DisplayName("Test D — Configuration: application-prod.yml explicitly disables startup prewarming")
    @SuppressWarnings("unchecked")
    void testD_productionConfigurationExplicitlyDisabled() throws Exception {
        ClassPathResource prodResource = new ClassPathResource("application-prod.yml");
        assertThat(prodResource.exists()).isTrue();

        Yaml yaml = new Yaml();
        try (InputStream is = prodResource.getInputStream()) {
            Map<String, Object> root = yaml.load(is);
            Map<String, Object> zynpath = (Map<String, Object>) root.get("zynpath");
            assertThat(zynpath).isNotNull();

            Map<String, Object> multiplayer = (Map<String, Object>) zynpath.get("multiplayer");
            assertThat(multiplayer).isNotNull();

            Map<String, Object> puzzleInventory = (Map<String, Object>) multiplayer.get("puzzle-inventory");
            assertThat(puzzleInventory).isNotNull();

            Boolean startupPrewarm = (Boolean) puzzleInventory.get("startup-prewarm-enabled");
            assertThat(startupPrewarm).isFalse();
        }

        // Also verify base application.yml declares fail-safe default
        ClassPathResource baseResource = new ClassPathResource("application.yml");
        assertThat(baseResource.exists()).isTrue();
        try (InputStream is = baseResource.getInputStream()) {
            Map<String, Object> root = yaml.load(is);
            Map<String, Object> zynpath = (Map<String, Object>) root.get("zynpath");
            assertThat(zynpath).isNotNull();

            Map<String, Object> multiplayer = (Map<String, Object>) zynpath.get("multiplayer");
            assertThat(multiplayer).isNotNull();

            Map<String, Object> puzzleInventory = (Map<String, Object>) multiplayer.get("puzzle-inventory");
            assertThat(puzzleInventory).isNotNull();

            Object startupPrewarm = puzzleInventory.get("startup-prewarm-enabled");
            assertThat(startupPrewarm).isNotNull();
            assertThat(startupPrewarm.toString()).contains("false");
        }
    }
}
