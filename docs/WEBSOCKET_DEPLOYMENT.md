# WebSocket Deployment & Real-Time Transport Specification

## 1. Real-Time Transport Architecture
Zynpath implements real-time multiplayer duels (Quick Duel, Friend Duel, Mini League) using Spring’s standard WebSocket framework configured in [WebSocketConfig.java](file:///d:/Zynpath/backend/src/main/java/com/zynpath/backend/social/websocket/WebSocketConfig.java) and handled by [MultiplayerWebSocketHandler.java](file:///d:/Zynpath/backend/src/main/java/com/zynpath/backend/social/websocket/MultiplayerWebSocketHandler.java).

- **Path:** `/ws/multiplayer`
- **Protocol:** Binary/Text JSON frame transport over native WebSockets (`wss://`).

---

## 2. Ingress & Reverse Proxy Upgrade Requirements
Standard HTTP reverse proxies (Nginx, Traefik, AWS ALB, Caddy, Cloudflare) terminate idle connections unless explicitly configured for WebSocket upgrades:

### Nginx Reverse Proxy Configuration Example:
```nginx
location /ws/multiplayer {
    proxy_pass http://zynpath-backend:8080/ws/multiplayer;
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection "Upgrade";
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;

    # Extend idle timeouts to prevent premature socket closure
    proxy_read_timeout 3600s;
    proxy_send_timeout 3600s;
}
```

---

## 3. Security & Origin Policy
1. **Explicit Origin Filtering:**
   In [WebSocketConfig.java](file:///d:/Zynpath/backend/src/main/java/com/zynpath/backend/social/websocket/WebSocketConfig.java), allowed origins are injected via `${zynpath.websocket.allowed-origins}`. In production, wildcard `*` is prohibited, restricting connections to verified client origins (e.g. `https://zynpath.app`).
2. **Handshake & Session Authentication:**
   - Clients pass their session bearer token in the initial upgrade request or first `AUTH` frame.
   - Handshake validates identity against `player_accounts`.
3. **Session Ownership Enforcement:**
   - A player can only join and send moves to match IDs they are registered in as a participant.
   - Any attempt to eavesdrop or publish moves to unauthorized match IDs yields immediate frame rejection and socket termination.

---

## 4. Multi-Instance Limitation & Single-Instance Baseline

> [!WARNING]
> **Architectural Constraint: In-Memory Match State**
> Zynpath’s active real-time match state, matchmaking queues, and participant session maps are currently maintained in JVM process memory using concurrent collections (`ConcurrentHashMap`).

### Operational Implications:
- **Single-Instance Baseline:** The initial production release operates as a single Spring Boot container instance. This meets the capacity requirements for thousands of concurrent duels without requiring distributed brokers.
- **Horizontal Scaling Prerequisite:** Scaling beyond a single container requires:
  1. A shared state and messaging layer (e.g. Redis Pub/Sub or Valkey), OR
  2. Strict sticky-session routing at the edge load balancer.
- **Restart Recovery Behavior:** If the backend container restarts during an active match:
  - In-flight WebSocket connections are dropped with standard close code `1001` (Going Away).
  - The Android client’s reconnection engine attempts exponential backoff.
  - Matches unfinalized upon restart are marked as aborted or resolved per [FAILURE_RECOVERY_POLICY.md](file:///d:/Zynpath/docs/FAILURE_RECOVERY_POLICY.md); the server never fabricates results.

---

## 5. Server Authority & Anti-Tamper Guarantees
The backend retains absolute authority over multiplayer puzzle gameplay:
- **Identical Puzzle Delivery:** Both duelists receive the exact same solver-verified seed and layout.
- **Server-Side Clock:** Timers, elapsed duration, and round countdowns are calculated server-side; client device clock shifts are ignored.
- **Final Validation:** Solutions submitted over WebSocket are re-executed against the puzzle solver engine prior to awarding rank or updating competitive stats.
