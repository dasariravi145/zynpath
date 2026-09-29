# Infrastructure Architecture & Cost Control Analysis

## 1. Objective & Financial Strategy
Zynpath's infrastructure architecture is intentionally designed for extreme operational efficiency and low-overhead launch. By utilizing a Spring Boot modular monolith, avoiding microservices, and employing in-memory session matching for active duels, the backend can operate reliably at minimal cost.

No cloud resources or paid infrastructure are provisioned during development.

---

## 2. Recurring Cost Categories Analysis

| Category | Primary Cost Drivers | Minimization Strategy in Zynpath Architecture |
|---|---|---|
| **Backend Compute** | CPU/RAM utilization during puzzle solving and WebSocket handling. | Lightweight Java 17 container; HikariCP connection pooling; solver optimizations; 1 instance handles thousands of daily players. |
| **Managed Database** | Relational storage (GB) and IOPS for PostgreSQL. | Compact schema (`V1`–`V6`); indexed queries; cold data archiving; minimal row width. |
| **Egress Bandwidth** | Data transferred to Android mobile clients. | Binary/compact JSON payloads; local puzzle assets cached on device; offline-first gameplay. |
| **Object Storage & Backups** | Compressed database snapshots and WAL stream retention. | Standard 30-day retention with lifecycle rules transitioning older backups to cold archive. |
| **Observability & Logging** | Ingestion fees for log and metric platforms. | Zero reliance on paid APMs; native Prometheus scraping and standard stdout logging. |
| **Push Notifications & Auth** | API calls to push and OAuth providers. | Free tier utilization (Firebase Cloud Messaging, Google Identity Platform). |

---

## 3. Practical Initial Hosting Options Evaluation

### Option A: Managed Container Service (e.g., AWS App Runner / Google Cloud Run / DigitalOcean App Platform)
- **WebSocket Feasibility:** Fully supported with HTTP/1.1 WebSocket upgrades.
- **Operational Complexity:** Very Low (no OS patching, automated TLS certificate renewal).
- **Database Connection:** Connects over private VPC connector to managed PostgreSQL.
- **Estimated Profile:** Ideal for low-maintenance single-instance or auto-scaling environments.

### Option B: Dedicated Cloud Virtual Machine (e.g., 2 vCPU / 4 GB RAM Compute Instance)
- **WebSocket Feasibility:** Excellent; persistent TCP connections maintained directly without proxy timeouts.
- **Operational Complexity:** Moderate (requires Docker Compose or systemd service management and automated security updates).
- **Cost Profile:** Lowest fixed cost tier; predictable monthly billing regardless of request volume.

### Option C: Managed PaaS (e.g., Render / Fly.io / Railway)
- **WebSocket Feasibility:** Native edge WebSocket support.
- **Operational Complexity:** Lowest; Git push or container registry integration.
- **Cost Profile:** Tiered developer pricing; fast transition from staging to production.

---

## 4. Single-Instance Baseline Justification
- **Throughput:** A 2 vCPU / 4 GB JVM instance easily sustains 500–1,000 active concurrent WebSocket matches and thousands of HTTP sync requests per minute.
- **Cost Minimization:** Eliminates the immediate need for distributed brokers (RabbitMQ/Kafka), distributed caching (Redis clusters), and multi-zone cross-talk egress charges.
- **Scalability Path:** When concurrency demands horizontal scaling, the modular monolith architecture easily accommodates a Redis Pub/Sub adapter for `MultiplayerWebSocketHandler` without refactoring domain logic.
