# Backend Health & Readiness Checks

## 1. Overview
Zynpath exposes operational health endpoints using Spring Boot Actuator (`spring-boot-starter-actuator`). These endpoints allow container orchestrators (Docker, Kubernetes, AWS ECS) and edge load balancers to monitor process status and safely route traffic.

---

## 2. Health Endpoint Architecture

The health subsystem is split into two specialized probes:

| Probe Endpoint | Purpose | Trigger Conditions | Failure Action |
|---|---|---|---|
| `/actuator/health/liveness` | Determines whether the JVM process is alive and responsive. | Internal thread deadlock, fatal JVM error, memory exhaustion. | Container runtime restarts the process. |
| `/actuator/health/readiness` | Determines whether the application can safely accept user traffic. | Database connectivity healthy, Flyway migrations applied, Hikari pool active. | Load balancer stops routing ingress traffic; process is NOT restarted. |

---

## 3. Production Security & Data Obfuscation
In production (`application-prod.yml`), internal health details are strictly masked from public inspection:

```yaml
management:
  endpoint:
    health:
      show-details: never
      probes:
        enabled: true
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

### Security Guarantees:
- **Zero Topology Leakage:** An unauthenticated `GET /actuator/health` returns only `{"status":"UP"}` or `{"status":"DOWN"}` without database IP addresses, disk paths, or pool statistics.
- **Detailed Diagnostics:** Internal component breakdowns (disk space, db status, ping) are restricted to authenticated internal networks or Prometheus scrapers.

---

## 4. Container Orchestrator Integration

### Docker Compose Healthcheck Configuration
In [backend/docker-compose.prod.yml](file:///d:/Zynpath/backend/docker-compose.prod.yml):
```yaml
healthcheck:
  test: ["CMD-SHELL", "wget -q --spider http://localhost:8080/actuator/health/readiness || exit 1"]
  interval: 10s
  timeout: 5s
  retries: 3
  start_period: 25s
```

### Kubernetes Pod Spec Definition
```yaml
livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 20
  periodSeconds: 10
  timeoutSeconds: 3
  failureThreshold: 3

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 15
  periodSeconds: 5
  timeoutSeconds: 3
  failureThreshold: 2
```

---

## 5. External Dependency Resilience
- **Optional Services:** Third-party failures (e.g., Firebase Cloud Messaging API or AdMob SSV key download latency) do NOT fail the core readiness probe.
- **Critical Path:** Only the PostgreSQL relational database and internal puzzle generation/solver engines are bound to readiness. If an optional third-party service degrades, Zynpath continues serving offline sync, solo puzzle solving, and local play.
