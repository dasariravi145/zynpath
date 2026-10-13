package com.zynpath.backend.persistence.adapter;

import com.zynpath.backend.persistence.entity.PlayerLevelProgressEntity;
import com.zynpath.backend.persistence.entity.PlayerLevelProgressId;
import com.zynpath.backend.persistence.entity.SyncOperationEntity;
import com.zynpath.backend.persistence.repository.PlayerLevelProgressRepository;
import com.zynpath.backend.persistence.repository.SyncOperationRepository;
import com.zynpath.backend.sync.model.SyncDto.LevelProgressDto;
import com.zynpath.backend.sync.model.SyncDto.SyncOperationResultDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JpaSyncPersistenceAdapterTest {

    private PlayerLevelProgressRepository levelProgressRepository;
    private SyncOperationRepository syncOperationRepository;
    private JpaSyncPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        levelProgressRepository = mock(PlayerLevelProgressRepository.class);
        syncOperationRepository = mock(SyncOperationRepository.class);
        adapter = new JpaSyncPersistenceAdapter(levelProgressRepository, syncOperationRepository);
    }

    @Test
    void testGetCachedOperation_found() {
        SyncOperationEntity entity = new SyncOperationEntity(
                "op_1", "p_1", "SOLO_LEVEL_COMPLETION", "SUCCESS", null, 123456L
        );
        when(syncOperationRepository.findById("op_1")).thenReturn(Optional.of(entity));

        SyncOperationResultDto result = adapter.getCachedOperation("op_1");

        assertNotNull(result);
        assertEquals("op_1", result.operationId());
        assertEquals("SUCCESS", result.status());
    }

    @Test
    void testGetCachedOperation_notFound() {
        when(syncOperationRepository.findById("op_missing")).thenReturn(Optional.empty());

        SyncOperationResultDto result = adapter.getCachedOperation("op_missing");

        assertNull(result);
    }

    @Test
    void testSaveOrUpdateLevelProgress_mergesNonDestructively() {
        String playerId = "p_solo_1";
        PlayerLevelProgressId id = new PlayerLevelProgressId(playerId, 41);

        PlayerLevelProgressEntity existing = new PlayerLevelProgressEntity(
                playerId, 41, 1, 2, 55000L, 20, true, 1, 100000L, 100000L
        );
        when(levelProgressRepository.findById(id)).thenReturn(Optional.of(existing));

        // Incoming attempt: better stars (3), faster time (42000L), fewer moves (18), 0 hints used
        LevelProgressDto incoming = new LevelProgressDto(
                41, 1, 3, 42000L, 18, true, 0, 110000L
        );

        adapter.saveOrUpdateLevelProgress(playerId, incoming);

        ArgumentCaptor<PlayerLevelProgressEntity> captor = ArgumentCaptor.forClass(PlayerLevelProgressEntity.class);
        verify(levelProgressRepository).save(captor.capture());

        PlayerLevelProgressEntity saved = captor.getValue();
        assertEquals(3, saved.getStars()); // Max stars
        assertEquals(42000L, saved.getBestTimeMs()); // Faster time
        assertEquals(18, saved.getMovesCount()); // Fewer moves
        assertEquals(0, saved.getBestHintCount()); // Min hints used
        assertEquals(100000L, saved.getCompletedAtMs()); // Earliest completion timestamp preserved
        assertTrue(saved.isCompleted());
    }

    @Test
    void testGetPlayerProgressList() {
        String playerId = "p_solo_2";
        PlayerLevelProgressEntity e1 = new PlayerLevelProgressEntity(playerId, 1, 1, 3, 15000L, 10, true, 0, 100L, 100L);
        PlayerLevelProgressEntity e2 = new PlayerLevelProgressEntity(playerId, 2, 1, 3, 18000L, 12, true, 0, 200L, 200L);
        when(levelProgressRepository.findByPlayerIdOrderByLevelIdAsc(playerId)).thenReturn(List.of(e1, e2));

        List<LevelProgressDto> list = adapter.getPlayerProgressList(playerId);

        assertEquals(2, list.size());
        assertEquals(1, list.get(0).levelId());
        assertEquals(2, list.get(1).levelId());
    }
}
