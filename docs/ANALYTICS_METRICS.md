# Zynpath: Analytics Metrics & Calculation Standards

## 1. Metric Definitions

### 1.1 Solo Progression Metrics
- **Completed Levels ($C$):** Count of unique canonical levels in range $[1, 300]$ where `isCompleted == true`.
- **Completion Percentage:** $\frac{C}{300} \times 100\%$. Denominator is strictly 300 for canonical progression.
- **Active World:** First world $[1, 6]$ containing an incomplete canonical level, or World 6 if all 300 are completed.
- **Completed Worlds:** Count of worlds where all levels within that world's canonical range are solved.

### 1.2 Timing Semantics
- **Solve Time ($T$):** The total active solving duration in milliseconds recorded upon level completion.
- **Legacy / Missing Times:** If a historical completion record lacks a valid time (`bestTimeMs == 0L`), it is **strictly excluded** from averages and fastest calculations rather than converted to zero.
- **Fastest Solve:** $\min(T_i)$ for all $T_i > 0$.
- **Average Solve Time:** $\frac{\sum_{i=1}^K T_i}{K}$ where $K$ is the count of completed levels with $T_i > 0$.

### 1.3 Time Improvement Comparisons
- **Improvement Delta:** $T_{\text{first}} - T_{\text{best}}$.
- **Improvement Percentage:** $\frac{T_{\text{first}} - T_{\text{best}}}{T_{\text{first}}} \times 100\%$.
- **Strict Validity Condition:** Comparisons are **only** valid when comparing attempts on the **same puzzle identity and identical puzzle version**. Different levels or layouts are never compared as equivalent.

### 1.4 Completion Trends
- **First-Time Solves:** Puzzles completed for the very first time in that calendar window.
- **Replay Solves:** Completed attempts on levels that were already solved prior to the attempt.
- **Total Sessions:** $\text{First-Time} + \text{Replays}$.

---

## 2. Competitive Metric Formulas

### 2.1 1v1 Duels (Quick Duel & Friend Duel)
- **Decisive Matches ($D$):** $\text{Matches Played} - \text{Ties}$.
- **Win Rate:**
  $$\text{Win Rate} = \begin{cases} \frac{\text{Wins}}{D} \times 100\% & \text{if } D > 0 \\ \text{N/A (—)} & \text{if } D = 0 \end{cases}$$
- **Ties:** Excluded from the win-rate denominator in accordance with competitive ranking standards.
- **Friend Duel Isolation:** Friend Duel records are stored and calculated independently. Private matches are never merged into Quick Duel totals or leaderboards.

### 2.2 Mini League (4-Player Tournament)
- **Podium Rate:** $\frac{\text{Top 3 Finishes}}{\text{Participations}} \times 100\%$ (where $\text{Participations} > 0$).
- **First Place Rate:** $\frac{\text{First Place Finishes}}{\text{Participations}} \times 100\%$.
- **Average Finish Position:** $\frac{\sum \text{Finish Order}}{\text{Participations}}$ (values between 1.0 and 4.0).
- **Non-Conflation:** Mini League results are never converted into a 1v1 win/loss record.

---

## 3. Sample Size ($N$) Mandate
Every percentage, average, or ratio displayed in the analytics dashboard must display its supporting sample size ($N$). When $N = 0$, the metric displays an explicit empty state (`—` or `0 decisive matches`) rather than implying zero ability or misleading perfection.
