# Zynpath Cost-Optimized Cloud & Runtime Architecture

**Status:** Mandatory Engineering Policy  
**Objective:** High Performance with Near-Zero Waste Infrastructure Cost  

---

## 1. Core Cost Philosophy

Zynpath is engineered to achieve massive user scalability with minimal operational overhead. Every network round-trip, database query, and compute cycle directly impacts operational expenses. The system avoids premature complexity and costly distributed dependencies.

---

## 2. The Seven Pillars of Zero-Waste Engineering

| Pillar | Engineering Directive | Cost Impact |
|---|---|---|
| **1. 100% Client-Side Solo Play** | All solo puzzle generation, solving, hint calculation, and level validation execute locally on the Android device. | **Zero server compute or cloud database reads/writes for solo play.** |
| **2. Zero Touch Streaming** | Never stream raw finger X/Y coordinates to the server. Multiplayer broadcasts only milestone progress (25%, 50%, 75%) and the final solution array. | **Reduces WebSocket network egress bandwidth by >99.5%.** |
| **3. Ephemeral In-Memory Matches** | Active Duel and Mini League rooms live in Java server RAM (`ConcurrentHashMap`). No database operations occur during active gameplay. | **Eliminates hundreds of thousands of active game state database writes.** |
| **4. Zero Text Chat Overhead** | Freeform chat is replaced by preset reaction tokens relayed ephemerally and discarded immediately after delivery. | **Zero chat database tables, zero moderation costs, zero cloud backup storage fees.** |
| **5. Lean Persistence Boundary** | Cloud persistence is restricted exclusively to: Profiles, Friends, Final Match Results, League Standings, and Subscription Entitlements. | **Maintains database transactions at minimum viable footprint.** |
| **6. Monolithic Simplicity** | A single Spring Boot service handles matchmaking, WebSockets, REST APIs, and validation. | **Avoids multi-cluster Kubernetes, VPC peering, and service-mesh cloud bills.** |
| **7. Standalone In-Memory Caching** | Matchmaking queues and session tokens are managed via JVM native concurrency (`ConcurrentLinkedQueue`) rather than managed Redis. | **Saves $30–$100/month in idle managed cache infrastructure.** |

---

## 3. Database Evaluation: PostgreSQL vs. Cloud Firestore

To ensure database cost-efficiency, Zynpath accesses all persistence operations through abstract **Repository Interfaces** (`PlayerRepository`, `MatchResultRepository`, `FriendRepository`), decoupling business logic from underlying storage.

```mermaid
graph TD
    Service[Spring Boot Domain Services] --> RepoInterface[Repository Interface Abstraction]
    RepoInterface -.->|Local Dev| LocalDB[(Local H2 / PostgreSQL)]
    RepoInterface -.->|Option A (Cost-Predictable)| CloudPostgres[(Managed PostgreSQL / Supabase)]
    RepoInterface -.->|Option B (Serverless Scale)| Firestore[(Google Cloud Firestore)]
```

### 3.1 Comparative Cost Analysis

| Dimension | Cloud Firestore (Document DB) | Managed PostgreSQL (Relational) |
|---|---|---|
| **Pricing Model** | Per read / write / delete operation. | Flat predictable monthly VM / storage tier. |
| **Best For** | Bursty traffic, low initial usage, serverless auto-scale. | High concurrent writes, complex relational leaderboards. |
| **Risk Factor** | Unexpected traffic spikes can trigger high billing if queries are unindexed or looped. | Fixed monthly cost even during traffic lulls. |
| **Development Phase** | Simulated locally with Firestore Emulator. | Simulated locally with H2 / Testcontainers / Local Docker. |
| **Architectural Decision** | **Abstract via Spring Data Repository.** Evaluate actual traffic profile during beta testing before locking production provider. |

---

## 4. What Zynpath Explicitly Does NOT Use (Avoided Over-Engineering)

- ❌ **No Kubernetes (K8s)**: Spring Boot container runs smoothly on a basic container service (e.g., Cloud Run, Railway, or standard VM).
- ❌ **No Dedicated Redis Cluster**: Java memory handles concurrent rooms and queues effortlessly at target scale.
- ❌ **No Distributed Message Brokers (Kafka / RabbitMQ)**: Direct WebSocket session dispatch is sufficient for 2–5 player rooms.
- ❌ **No Heavy Microservices**: Avoiding microservice network hops and multiplied baseline container charges.
- ❌ **No Continuous Cloud Level Generators**: Level generation seeds are bundled client-side with algorithmic determinism.

---

## 5. Pre-Production Budgeting & Guardrails

Prior to production cloud deployment:
1. **GCP / Cloud Billing Alerts**: Configure hard budget alerts at \$10, \$50, and \$100 thresholds.
2. **Connection Pooling**: Strict database connection limits (HikariCP max pool size = 10 per instance).
3. **Payload Gzip / Brotli Compression**: Compression enabled on all HTTP and WebSocket payloads.
4. **AdMob Revenue Self-Sustaining Ratio**: Target cloud infrastructure cost per active user must remain below \$0.005/month, easily offset by organic AdMob CPM and Premium subscriptions.
