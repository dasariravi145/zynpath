package com.zynpath.backend.multiplayer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Authoritative backend room model for Friends Arena.
 *
 * Implements Prompt 18 Task 2:
 * - Internal room ID.
 * - Unique public room code.
 * - Host player ID.
 * - Room status (WAITING, STARTING, IN_GAME, CLOSED).
 * - Created timestamp.
 * - Current member identities & member join order.
 * - Version counter for optimistic/concurrency protection.
 * - Room rules:
 *   - Host is the first member.
 *   - Maximum five distinct members, INCLUDING host.
 *   - A player cannot occupy duplicate slots.
 *   - A room can wait with one member.
 *   - Multiplayer match requires 2-5 members.
 *   - Only the host may request match start.
 */
public class FriendsArenaRoom {

    public static final int MAX_CAPACITY = 5;
    public static final int MIN_PLAYERS_TO_START = 2;

    private final String roomId;
    private final String roomCode;
    private volatile String hostPlayerId;
    private volatile String activeMatchId;
    private volatile FriendsArenaRoomStatus status;
    private final long createdAt;
    private final AtomicLong version = new AtomicLong(1);
    private final List<FriendsArenaMember> members = new CopyOnWriteArrayList<>();

    public FriendsArenaRoom(
            String roomId,
            String roomCode,
            String hostPlayerId,
            long createdAt
    ) {
        this.roomId = roomId;
        this.roomCode = roomCode;
        this.hostPlayerId = hostPlayerId;
        this.status = FriendsArenaRoomStatus.WAITING;
        this.createdAt = createdAt;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public String getHostPlayerId() {
        return hostPlayerId;
    }

    public void setHostPlayerId(String hostPlayerId) {
        this.hostPlayerId = hostPlayerId;
    }

    public String getActiveMatchId() {
        return activeMatchId;
    }

    public void setActiveMatchId(String activeMatchId) {
        this.activeMatchId = activeMatchId;
    }

    public FriendsArenaRoomStatus getStatus() {
        return status;
    }

    public void setStatus(FriendsArenaRoomStatus status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getVersion() {
        return version.get();
    }

    public long incrementVersion() {
        return version.incrementAndGet();
    }

    public List<FriendsArenaMember> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public int getMemberCount() {
        return members.size();
    }

    public boolean hasMember(String playerId) {
        if (playerId == null) return false;
        for (FriendsArenaMember m : members) {
            if (playerId.equals(m.playerId())) {
                return true;
            }
        }
        return false;
    }

    public FriendsArenaMember getMember(String playerId) {
        if (playerId == null) return null;
        for (FriendsArenaMember m : members) {
            if (playerId.equals(m.playerId())) {
                return m;
            }
        }
        return null;
    }

    /**
     * Atomically adds a new member if not already present and below maximum capacity of 5.
     */
    public synchronized boolean addMember(FriendsArenaMember member) {
        if (hasMember(member.playerId())) {
            return false;
        }
        if (members.size() >= MAX_CAPACITY) {
            return false;
        }
        members.add(member);
        return true;
    }

    /**
     * Atomically removes a member from the room.
     */
    public synchronized boolean removeMember(String playerId) {
        return members.removeIf(m -> m.playerId().equals(playerId));
    }

    public MultiplayerDto.FriendsArenaRoomDto toDto() {
        List<MultiplayerDto.FriendsArenaMemberDto> memberDtos = new ArrayList<>();
        for (FriendsArenaMember m : members) {
            memberDtos.add(m.toDto());
        }
        return new MultiplayerDto.FriendsArenaRoomDto(
                roomId,
                roomCode,
                hostPlayerId,
                status.name(),
                members.size(),
                MAX_CAPACITY,
                memberDtos,
                activeMatchId,
                createdAt,
                version.get()
        );
    }
}
