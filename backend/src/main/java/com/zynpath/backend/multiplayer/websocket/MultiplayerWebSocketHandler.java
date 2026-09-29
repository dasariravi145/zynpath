package com.zynpath.backend.multiplayer.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zynpath.backend.auth.model.PlayerSession;
import com.zynpath.backend.auth.service.SessionSecurityService;
import com.zynpath.backend.multiplayer.model.FriendsArenaMatch;
import com.zynpath.backend.multiplayer.model.FriendsArenaRoom;
import com.zynpath.backend.multiplayer.model.MatchSession;
import com.zynpath.backend.multiplayer.model.MultiplayerDto;
import com.zynpath.backend.multiplayer.model.MultiplayerEventEnvelope;
import com.zynpath.backend.multiplayer.model.MultiplayerEventType;
import com.zynpath.backend.multiplayer.service.MatchEventDispatcher;
import com.zynpath.backend.multiplayer.service.MatchSessionService;
import com.zynpath.backend.puzzle.model.ValidationOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Real-time WebSocket transport handler for online multiplayer sessions and matchmaking events.
 *
 * Implements Prompt 20 Sections 27-31:
 * - Session token authentication on connection.
 * - Match-level subscription authorization: prevents observing unauthorized private matches.
 * - Dispatches typed versioned event envelopes.
 * - Handles real-time ready, coarse progress, authoritative claim submission, and reconnection.
 */
@Component
public class MultiplayerWebSocketHandler extends TextWebSocketHandler implements MatchEventDispatcher {

    private static final Logger log = LoggerFactory.getLogger(MultiplayerWebSocketHandler.class);

    private final SessionSecurityService sessionSecurityService;
    private final MatchSessionService matchSessionService;
    private final com.zynpath.backend.multiplayer.service.FriendDuelService friendDuelService;
    private final com.zynpath.backend.multiplayer.service.FriendsArenaService friendsArenaService;
    private final ObjectMapper objectMapper;
    private final com.zynpath.backend.security.audit.SecurityAuditLogger auditLogger;

    private static final int MAX_WS_MESSAGES_PER_SECOND = 20;
    private final Map<String, java.util.concurrent.atomic.AtomicInteger> socketMessageCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> socketWindowStarts = new ConcurrentHashMap<>();

    // Track active connection: socketId -> playerId
    private final Map<String, String> socketPlayerMap = new ConcurrentHashMap<>();
    // Track active player socket: playerId -> WebSocketSession
    private final Map<String, WebSocketSession> playerSocketMap = new ConcurrentHashMap<>();
    // Track player subscriptions: matchId -> Set of playerIds
    private final Map<String, ConcurrentHashMap.KeySetView<String, Boolean>> matchSubscribers = new ConcurrentHashMap<>();
    // Track Friends Arena room subscriptions: roomId -> Set of playerIds
    private final Map<String, ConcurrentHashMap.KeySetView<String, Boolean>> friendsArenaRoomSubscribers = new ConcurrentHashMap<>();

    public MultiplayerWebSocketHandler(
            SessionSecurityService sessionSecurityService,
            @Lazy MatchSessionService matchSessionService,
            @Lazy com.zynpath.backend.multiplayer.service.FriendDuelService friendDuelService,
            @Lazy com.zynpath.backend.multiplayer.service.FriendsArenaService friendsArenaService,
            ObjectMapper objectMapper,
            com.zynpath.backend.security.audit.SecurityAuditLogger auditLogger
    ) {
        this.sessionSecurityService = sessionSecurityService;
        this.matchSessionService = matchSessionService;
        this.friendDuelService = friendDuelService;
        this.friendsArenaService = friendsArenaService;
        this.objectMapper = objectMapper;
        this.auditLogger = auditLogger;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = extractTokenFromUri(session.getUri());
        if (token != null && !token.isBlank()) {
            authenticateSocket(session, token);
        } else {
            sendEnvelope(session, MultiplayerEventEnvelope.create(
                    MultiplayerEventType.ERROR,
                    null,
                    0,
                    Map.of("code", "AUTH_REQUIRED", "message", "Send AUTH message or pass token in URL")
            ));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Enforce WebSocket per-socket message rate limiting (Prompt 36 Section 28)
        long now = System.currentTimeMillis();
        long windowStart = socketWindowStarts.computeIfAbsent(session.getId(), k -> now);
        if (now - windowStart > 1000L) {
            socketWindowStarts.put(session.getId(), now);
            socketMessageCounts.put(session.getId(), new java.util.concurrent.atomic.AtomicInteger(1));
        } else {
            java.util.concurrent.atomic.AtomicInteger count = socketMessageCounts.computeIfAbsent(session.getId(), k -> new java.util.concurrent.atomic.AtomicInteger(0));
            if (count.incrementAndGet() > MAX_WS_MESSAGES_PER_SECOND) {
                sendError(session, "RATE_LIMITED", "Too many messages sent. Please slow down.");
                auditLogger.recordEvent(
                        com.zynpath.backend.security.audit.SecurityEventType.RATE_LIMIT_EXCEEDED,
                        socketPlayerMap.get(session.getId()),
                        session.getRemoteAddress() != null ? session.getRemoteAddress().toString() : "unknown",
                        "websocket:multiplayer",
                        "THROTTLED",
                        Map.of("messageRateLimit", MAX_WS_MESSAGES_PER_SECOND)
                );
                return;
            }
        }

        try {
            JsonNode root = objectMapper.readTree(message.getPayload());
            String type = root.has("type") ? root.get("type").asText() : "";
            String playerId = socketPlayerMap.get(session.getId());

            switch (type) {
                case "AUTH" -> {
                    String token = root.has("token") ? root.get("token").asText() : "";
                    authenticateSocket(session, token);
                }
                case "SUBSCRIBE_MATCH" -> {
                    if (playerId == null) {
                        sendError(session, "UNAUTHENTICATED", "Authenticate before subscribing to matches");
                        return;
                    }
                    String matchId = root.has("matchId") ? root.get("matchId").asText() : "";
                    subscribeToMatch(session, playerId, matchId);
                }
                case "READY" -> {
                    if (playerId == null) return;
                    String matchId = root.has("matchId") ? root.get("matchId").asText() : "";
                    if (!assertParticipant(session, matchId, playerId, "READY")) return;
                    matchSessionService.markPlayerReady(matchId, playerId);
                }
                case "PROGRESS" -> {
                    if (playerId == null) return;
                    String matchId = root.has("matchId") ? root.get("matchId").asText() : "";
                    int covered = root.has("coveredCells") ? root.get("coveredCells").asInt() : 0;
                    int checkpoint = root.has("lastCheckpoint") ? root.get("lastCheckpoint").asInt() : 1;
                    if (matchId.startsWith("famatch_")) {
                        friendsArenaService.updatePlayerProgress(matchId, playerId, covered, checkpoint);
                    } else {
                        if (!assertParticipant(session, matchId, playerId, "PROGRESS")) return;
                        matchSessionService.updateProgress(matchId, playerId, covered, checkpoint);
                    }
                }
                case "CLAIM" -> {
                    if (playerId == null) return;
                    String matchId = root.has("matchId") ? root.get("matchId").asText() : "";
                    long reportedTime = root.has("clientReportedSolveTimeMs") ? root.get("clientReportedSolveTimeMs").asLong() : 0L;
                    List<String> path = new ArrayList<>();
                    if (root.has("pathCoordinates") && root.get("pathCoordinates").isArray()) {
                        for (JsonNode n : root.get("pathCoordinates")) {
                            path.add(n.asText());
                        }
                    }

                    if (matchId.startsWith("famatch_")) {
                        ValidationOutcome outcome = friendsArenaService.submitSolutionClaim(matchId, playerId, path, reportedTime);
                        if (!outcome.valid()) {
                            sendError(session, "INVALID_CLAIM", outcome.rejectionReason());
                        }
                    } else {
                        if (!assertParticipant(session, matchId, playerId, "CLAIM")) return;
                        String puzzleId = root.has("puzzleId") ? root.get("puzzleId").asText() : "";
                        int moves = root.has("movesCount") ? root.get("movesCount").asInt() : 0;
                        MultiplayerDto.SubmitSolutionClaimRequest claimReq = new MultiplayerDto.SubmitSolutionClaimRequest(
                                matchId, puzzleId, path, reportedTime, moves
                        );
                        ValidationOutcome outcome = matchSessionService.submitCompletionClaim(matchId, playerId, claimReq);
                        if (!outcome.valid()) {
                            sendError(session, "INVALID_CLAIM", outcome.rejectionReason());
                        }
                    }
                }
                case "RECONNECT" -> {
                    if (playerId == null) return;
                    String matchId = root.has("matchId") ? root.get("matchId").asText() : "";
                    if (matchId.startsWith("famatch_")) {
                        FriendsArenaMatch famatch = friendsArenaService.getMatchEntity(matchId);
                        if (famatch != null && famatch.hasParticipant(playerId)) {
                            subscribeToFriendsArenaRoom(session, playerId, famatch.getSourceRoomId());
                            friendsArenaService.handlePlayerReconnect(matchId, playerId);
                        }
                    } else {
                        if (!assertParticipant(session, matchId, playerId, "RECONNECT")) return;
                        subscribeToMatch(session, playerId, matchId);
                        matchSessionService.reconnectPlayer(matchId, playerId);
                    }
                }
                case "FORFEIT" -> {
                    if (playerId == null) return;
                    String matchId = root.has("matchId") ? root.get("matchId").asText() : "";
                    if (matchId.startsWith("famatch_")) {
                        friendsArenaService.forfeitMatch(matchId, playerId);
                    } else {
                        if (!assertParticipant(session, matchId, playerId, "FORFEIT")) return;
                        matchSessionService.forfeitMatch(matchId, playerId);
                    }
                }
                case "PRESET_REACTION", "REACTION" -> {
                    if (playerId == null) return;
                    String matchId = root.has("matchId") ? root.get("matchId").asText() : "";
                    if (!assertParticipant(session, matchId, playerId, "REACTION")) return;
                    String code = root.has("reactionCode") ? root.get("reactionCode").asText() : "";

                    MatchSession match = matchSessionService.getSession(matchId);
                    if (match != null && match.hasParticipant(playerId)) {
                        long seq = match.nextSequenceNumber();
                        dispatchToMatch(matchId, MultiplayerEventEnvelope.create(
                                MultiplayerEventType.REACTION_EMITTED,
                                matchId,
                                seq,
                                Map.of("playerId", playerId, "reactionCode", code)
                        ));
                    }
                }
                case "SEND_INVITATION" -> {
                    if (playerId == null) return;
                    String targetPublicId = root.has("targetPublicZynpathId") ? root.get("targetPublicZynpathId").asText() : "";
                    try {
                        friendDuelService.sendInvitation(playerId, targetPublicId);
                    } catch (Exception e) {
                        sendError(session, "INVITATION_FAILED", e.getMessage());
                    }
                }
                case "ACCEPT_INVITATION" -> {
                    if (playerId == null) return;
                    String invitationId = root.has("invitationId") ? root.get("invitationId").asText() : "";
                    try {
                        friendDuelService.acceptInvitation(invitationId, playerId);
                    } catch (Exception e) {
                        sendError(session, "ACCEPT_FAILED", e.getMessage());
                    }
                }
                case "DECLINE_INVITATION" -> {
                    if (playerId == null) return;
                    String invitationId = root.has("invitationId") ? root.get("invitationId").asText() : "";
                    try {
                        friendDuelService.declineInvitation(invitationId, playerId);
                    } catch (Exception e) {
                        sendError(session, "DECLINE_FAILED", e.getMessage());
                    }
                }
                case "CANCEL_INVITATION" -> {
                    if (playerId == null) return;
                    String invitationId = root.has("invitationId") ? root.get("invitationId").asText() : "";
                    try {
                        friendDuelService.cancelInvitation(invitationId, playerId);
                    } catch (Exception e) {
                        sendError(session, "CANCEL_FAILED", e.getMessage());
                    }
                }
                case "REQUEST_REMATCH" -> {
                    if (playerId == null) return;
                    String matchId = root.has("matchId") ? root.get("matchId").asText() : "";
                    try {
                        friendDuelService.requestRematch(matchId, playerId);
                    } catch (Exception e) {
                        sendError(session, "REMATCH_FAILED", e.getMessage());
                    }
                }
                case "RESPOND_REMATCH" -> {
                    if (playerId == null) return;
                    String matchId = root.has("matchId") ? root.get("matchId").asText() : "";
                    boolean accept = root.has("accept") && root.get("accept").asBoolean();
                    try {
                        friendDuelService.respondToRematch(matchId, playerId, accept);
                    } catch (Exception e) {
                        sendError(session, "REMATCH_RESPONSE_FAILED", e.getMessage());
                    }
                }
                case "SUBSCRIBE_ROOM", "SUBSCRIBE_FRIENDS_ARENA" -> {
                    if (playerId == null) {
                        sendError(session, "UNAUTHENTICATED", "Authenticate before subscribing to rooms");
                        return;
                    }
                    String roomId = root.has("roomId") ? root.get("roomId").asText() : "";
                    subscribeToFriendsArenaRoom(session, playerId, roomId);
                }
                default -> log.debug("Unknown multiplayer websocket action: {}", type);
            }
        } catch (Exception e) {
            log.warn("Error processing multiplayer websocket message: {}", e.getMessage());
            sendError(session, "MALFORMED_MESSAGE", e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        socketMessageCounts.remove(session.getId());
        socketWindowStarts.remove(session.getId());
        String playerId = socketPlayerMap.remove(session.getId());
        if (playerId != null) {
            playerSocketMap.remove(playerId);
            // Notify active subscribed matches
            for (Map.Entry<String, ConcurrentHashMap.KeySetView<String, Boolean>> entry : matchSubscribers.entrySet()) {
                if (entry.getValue().remove(playerId)) {
                    matchSessionService.handlePlayerDisconnect(entry.getKey(), playerId);
                }
            }
            // Remove from active Friends Arena room subscriptions
            for (Map.Entry<String, ConcurrentHashMap.KeySetView<String, Boolean>> entry : friendsArenaRoomSubscribers.entrySet()) {
                if (entry.getValue().remove(playerId)) {
                    FriendsArenaRoom room = friendsArenaService.getRoomEntity(entry.getKey());
                    if (room != null && room.getActiveMatchId() != null) {
                        friendsArenaService.handlePlayerDisconnect(room.getActiveMatchId(), playerId);
                    }
                }
            }
            log.info("Multiplayer websocket closed for playerId={}", playerId);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.warn("Multiplayer transport error: id={}, error={}", session.getId(), exception.getMessage());
        afterConnectionClosed(session, CloseStatus.SERVER_ERROR);
    }

    private boolean assertParticipant(WebSocketSession session, String matchId, String playerId, String action) {
        if (matchId == null || matchId.isBlank()) {
            sendError(session, "INVALID_MATCH_ID", "Match ID is required for action: " + action);
            return false;
        }
        MatchSession match = matchSessionService.getSession(matchId);
        if (match == null || !match.hasParticipant(playerId)) {
            sendError(session, "UNAUTHORIZED_ACTION", "You are not a participant in match: " + matchId);
            auditLogger.recordEvent(
                    com.zynpath.backend.security.audit.SecurityEventType.WEBSOCKET_UNAUTHORIZED,
                    playerId,
                    session.getRemoteAddress() != null ? session.getRemoteAddress().toString() : "unknown",
                    "match:" + matchId,
                    "DENIED",
                    Map.of("action", action)
            );
            return false;
        }
        return true;
    }

    private void authenticateSocket(WebSocketSession session, String token) {
        try {
            PlayerSession playerSession = sessionSecurityService.validateSession("Bearer " + token);
            String playerId = playerSession.playerId();

            WebSocketSession existing = playerSocketMap.put(playerId, session);
            if (existing != null && existing.isOpen() && !existing.getId().equals(session.getId())) {
                try {
                    existing.close(CloseStatus.POLICY_VIOLATION.withReason("Superseded by new session"));
                } catch (IOException ignored) {}
            }

            socketPlayerMap.put(session.getId(), playerId);

            sendEnvelope(session, MultiplayerEventEnvelope.create(
                    MultiplayerEventType.PLAYER_JOINED,
                    null,
                    0,
                    Map.of("playerId", playerId, "status", "AUTHENTICATED")
            ));
            log.info("Multiplayer websocket authenticated: playerId={}", playerId);
        } catch (Exception e) {
            log.warn("Multiplayer websocket authentication failed: {}", e.getMessage());
            sendError(session, "AUTH_FAILED", "Invalid session token");
            try {
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Auth failed"));
            } catch (IOException ignored) {}
        }
    }

    private void subscribeToMatch(WebSocketSession session, String playerId, String matchId) {
        try {
            MatchSession match = matchSessionService.getSession(matchId);
            if (!match.hasParticipant(playerId)) {
                sendError(session, "UNAUTHORIZED_MATCH", "You are not a participant in match: " + matchId);
                return;
            }

            matchSubscribers.computeIfAbsent(matchId, k -> ConcurrentHashMap.newKeySet()).add(playerId);
            log.info("Player {} subscribed to match events for matchId={}", playerId, matchId);
        } catch (Exception e) {
            sendError(session, "MATCH_NOT_FOUND", e.getMessage());
        }
    }

    private void subscribeToFriendsArenaRoom(WebSocketSession session, String playerId, String roomId) {
        try {
            if (roomId == null || roomId.isBlank()) {
                sendError(session, "INVALID_ROOM_ID", "Room ID is required");
                return;
            }
            if (!friendsArenaService.isMember(roomId, playerId)) {
                sendError(session, "UNAUTHORIZED_ROOM", "You are not an authorized member of room: " + roomId);
                auditLogger.recordEvent(
                        com.zynpath.backend.security.audit.SecurityEventType.WEBSOCKET_UNAUTHORIZED,
                        playerId,
                        session.getRemoteAddress() != null ? session.getRemoteAddress().toString() : "unknown",
                        "friends_arena_room:" + roomId,
                        "DENIED",
                        Map.of("action", "SUBSCRIBE_ROOM")
                );
                return;
            }

            friendsArenaRoomSubscribers.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(playerId);
            log.info("Player {} subscribed to Friends Arena room events for roomId={}", playerId, roomId);
        } catch (Exception e) {
            sendError(session, "ROOM_NOT_FOUND", e.getMessage());
        }
    }

    @Override
    public void dispatchToMatch(String matchId, MultiplayerEventEnvelope envelope) {
        ConcurrentHashMap.KeySetView<String, Boolean> subscribers = matchSubscribers.get(matchId);
        if (subscribers != null) {
            for (String playerId : subscribers) {
                dispatchToPlayer(playerId, envelope);
            }
        }
    }

    @Override
    public void dispatchToFriendsArenaRoom(String roomId, MultiplayerEventEnvelope envelope) {
        ConcurrentHashMap.KeySetView<String, Boolean> subscribers = friendsArenaRoomSubscribers.get(roomId);
        if (subscribers != null) {
            for (String playerId : subscribers) {
                dispatchToPlayer(playerId, envelope);
            }
        }
    }

    @Override
    public void dispatchToPlayer(String playerId, MultiplayerEventEnvelope envelope) {
        WebSocketSession session = playerSocketMap.get(playerId);
        if (session != null && session.isOpen()) {
            sendEnvelope(session, envelope);
        }
    }

    private void sendEnvelope(WebSocketSession session, MultiplayerEventEnvelope envelope) {
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(envelope)));
            } catch (IOException e) {
                log.warn("Failed to send multiplayer event: {}", e.getMessage());
            }
        }
    }

    private void sendError(WebSocketSession session, String code, String message) {
        sendEnvelope(session, MultiplayerEventEnvelope.create(
                MultiplayerEventType.ERROR,
                null,
                0,
                Map.of("code", code, "message", message != null ? message : "Error")
        ));
    }

    private String extractTokenFromUri(URI uri) {
        if (uri == null || uri.getQuery() == null) return null;
        for (String param : uri.getQuery().split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2 && "token".equalsIgnoreCase(pair[0])) {
                return pair[1];
            }
        }
        return null;
    }
}
