package com.zynpath.backend.persistence.port;

import com.zynpath.backend.sync.model.SyncDto.LevelProgressDto;
import com.zynpath.backend.sync.model.SyncDto.SyncOperationResultDto;

import java.util.List;

/**
 * Storage-agnostic persistence port for client synchronization operations and Solo level progress.
 */
public interface SyncPersistencePort {

    SyncOperationResultDto getCachedOperation(String operationId);

    void recordOperation(String playerId, SyncOperationResultDto result, String operationType);

    void saveOrUpdateLevelProgress(String playerId, LevelProgressDto progress);

    List<LevelProgressDto> getPlayerProgressList(String playerId);
}
