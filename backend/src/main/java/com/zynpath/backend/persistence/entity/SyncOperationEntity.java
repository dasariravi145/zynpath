package com.zynpath.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.util.Objects;

/**
 * Idempotent batch sync operation tracking record to prevent replay anomalies.
 * Maps to table: sync_operations
 */
@Entity
@Table(
        name = "sync_operations",
        indexes = {
                @Index(name = "idx_sync_op_player", columnList = "player_id"),
                @Index(name = "idx_sync_op_timestamp", columnList = "server_timestamp_ms")
        }
)
public class SyncOperationEntity {

    @Id
    @Column(name = "operation_id", length = 128, nullable = false)
    private String operationId;

    @Column(name = "player_id", length = 64, nullable = false)
    private String playerId;

    @Column(name = "operation_type", length = 64, nullable = false)
    private String operationType;

    @Column(name = "status", length = 32, nullable = false)
    private String status;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "server_timestamp_ms", nullable = false)
    private long serverTimestampMs;

    public SyncOperationEntity() {
    }

    public SyncOperationEntity(
            String operationId,
            String playerId,
            String operationType,
            String status,
            String message,
            long serverTimestampMs
    ) {
        this.operationId = operationId;
        this.playerId = playerId;
        this.operationType = operationType;
        this.status = status;
        this.message = message;
        this.serverTimestampMs = serverTimestampMs;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getServerTimestampMs() {
        return serverTimestampMs;
    }

    public void setServerTimestampMs(long serverTimestampMs) {
        this.serverTimestampMs = serverTimestampMs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SyncOperationEntity that = (SyncOperationEntity) o;
        return Objects.equals(operationId, that.operationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operationId);
    }
}
