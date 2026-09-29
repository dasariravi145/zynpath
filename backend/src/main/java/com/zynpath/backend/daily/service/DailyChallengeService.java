package com.zynpath.backend.daily.service;

import com.zynpath.backend.auth.model.AuthException;
import com.zynpath.backend.auth.model.PlayerAccount;
import com.zynpath.backend.auth.service.PlayerAccountService;
import com.zynpath.backend.daily.model.DailyChallengeDto;
import com.zynpath.backend.daily.model.DailyChallengeDto.DailyAttemptStatus;
import com.zynpath.backend.daily.model.DailyChallengeDto.DailyBlockedEdgeSpec;
import com.zynpath.backend.daily.model.DailyChallengeDto.DailyChallengeAttemptDto;
import com.zynpath.backend.daily.model.DailyChallengeDto.DailyChallengeDefinitionDto;
import com.zynpath.backend.daily.model.DailyChallengeDto.DailyChallengeResultDto;
import com.zynpath.backend.daily.model.DailyChallengeDto.DailyCheckpointSpec;
import com.zynpath.backend.daily.model.DailyChallengeDto.DailyCompletionClaimDto;
import com.zynpath.backend.daily.model.DailyChallengeDto.DailyLeaderboardEntryDto;
import com.zynpath.backend.daily.model.DailyChallengeDto.DailyLeaderboardResponse;
import com.zynpath.backend.daily.model.DailyChallengeDto.DailyProvisionalSyncRequest;
import com.zynpath.backend.daily.model.DailyChallengeDto.DailyVerificationStatus;
import com.zynpath.backend.multiplayer.model.PuzzleAssignment;
import com.zynpath.backend.multiplayer.puzzle.ServerPuzzleValidator;
import com.zynpath.backend.puzzle.model.ValidationOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Server-authoritative service for Daily Challenge publication, competitive attempt lifecycle,
 * independent solution verification, personal results, and daily leaderboards.
 *
 * Implements Prompt 25 Sections 6-17, 20-22, 28-39 & 52-54.
 */
@Service
public class DailyChallengeService {

    private static final Logger log = LoggerFactory.getLogger(DailyChallengeService.class);

    public static final String DEFAULT_SCHEDULE_VERSION = "1.0.0";
    public static final int CHALLENGE_VERSION = 1;
    public static final long ATTEMPT_TTL_MILLIS = 7_200_000L; // 2 hours

    private final PlayerAccountService playerAccountService;
    private final ServerPuzzleValidator serverPuzzleValidator;

    // Curated solver-verified daily puzzle pool
    private final List<DailyPuzzleEntry> dailyPool = new ArrayList<>();

    // In-memory thread-safe state stores
    private final Map<String, DailyChallengeAttemptDto> attemptsByAttemptId = new ConcurrentHashMap<>();
    private final Map<String, String> activeAttemptKeyToAttemptId = new ConcurrentHashMap<>(); // "playerId:dateKey" -> attemptId
    private final Map<String, DailyChallengeResultDto> finalResultKeyToResult = new ConcurrentHashMap<>(); // "playerId:dateKey" -> result
    private final Map<String, DailyChallengeResultDto> resultsByResultId = new ConcurrentHashMap<>();
    private final Map<String, List<DailyChallengeResultDto>> resultsByDateKey = new ConcurrentHashMap<>();
    // Section 60 & 63: In-memory cache for sorted eligible leaderboard results per dateKey
    private final Map<String, List<DailyChallengeResultDto>> sortedEligibleResultsByDate = new ConcurrentHashMap<>();

    public record DailyPuzzleEntry(
            String puzzleId,
            int puzzleVersion,
            String fingerprint,
            int rows,
            int cols,
            List<String> requiredCells,
            List<PuzzleAssignment.CheckpointSpec> checkpoints,
            List<PuzzleAssignment.BlockedEdgeSpec> blockedEdges,
            String difficultyTier,
            String title
    ) {}

    public DailyChallengeService(
            PlayerAccountService playerAccountService,
            ServerPuzzleValidator serverPuzzleValidator
    ) {
        this.playerAccountService = playerAccountService;
        this.serverPuzzleValidator = serverPuzzleValidator;
        initDailyPool();
    }

    private void initDailyPool() {
        List<String> cells4x4 = new ArrayList<>();
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                cells4x4.add(r + "," + c);
            }
        }

        List<String> cells5x5 = new ArrayList<>();
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                cells5x5.add(r + "," + c);
            }
        }

        List<String> cells6x6 = new ArrayList<>();
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                cells6x6.add(r + "," + c);
            }
        }

        // Index 0: PackagedPuzzles.LEVEL_1 (4x4, 5 checkpoints, 0 walls) - Serpentine Spark
        addDailyPoolEntry("w1_lvl1", 1, 4, 4, cells4x4,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 0, 0),
                        new PuzzleAssignment.CheckpointSpec(2, 0, 3),
                        new PuzzleAssignment.CheckpointSpec(3, 1, 0),
                        new PuzzleAssignment.CheckpointSpec(4, 2, 3),
                        new PuzzleAssignment.CheckpointSpec(5, 3, 0)
                ),
                List.of(),
                "BEGINNER", "Serpentine Spark");

        // Index 1: PackagedPuzzles.LEVEL_21 (5x5, 6 checkpoints, 0 walls) - Emerald Meadow
        addDailyPoolEntry("w2_lvl21", 1, 5, 5, cells5x5,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 0, 0),
                        new PuzzleAssignment.CheckpointSpec(2, 0, 4),
                        new PuzzleAssignment.CheckpointSpec(3, 1, 0),
                        new PuzzleAssignment.CheckpointSpec(4, 2, 4),
                        new PuzzleAssignment.CheckpointSpec(5, 3, 0),
                        new PuzzleAssignment.CheckpointSpec(6, 4, 4)
                ),
                List.of(),
                "MEDIUM", "Emerald Meadow");

        // Index 2: PackagedPuzzles.LEVEL_51 (5x5, 6 checkpoints, 1 wall) - Granite Gate
        addDailyPoolEntry("w3_lvl51", 1, 5, 5, cells5x5,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 0, 0),
                        new PuzzleAssignment.CheckpointSpec(2, 0, 4),
                        new PuzzleAssignment.CheckpointSpec(3, 1, 0),
                        new PuzzleAssignment.CheckpointSpec(4, 2, 4),
                        new PuzzleAssignment.CheckpointSpec(5, 3, 0),
                        new PuzzleAssignment.CheckpointSpec(6, 4, 4)
                ),
                List.of(new PuzzleAssignment.BlockedEdgeSpec(0, 1, 1, 1)),
                "CHALLENGING", "Granite Gate");

        // Index 3: PackagedPuzzles.LEVEL_2 (4x4, 5 checkpoints, 0 walls) - Corner Weaver
        addDailyPoolEntry("w1_lvl2", 1, 4, 4, cells4x4,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 0, 0),
                        new PuzzleAssignment.CheckpointSpec(2, 3, 0),
                        new PuzzleAssignment.CheckpointSpec(3, 0, 1),
                        new PuzzleAssignment.CheckpointSpec(4, 3, 2),
                        new PuzzleAssignment.CheckpointSpec(5, 0, 3)
                ),
                List.of(),
                "BEGINNER", "Corner Weaver");

        // Index 4: PackagedPuzzles.LEVEL_22 (5x5, 6 checkpoints, 0 walls) - Vertical Cascade
        addDailyPoolEntry("w2_lvl22", 1, 5, 5, cells5x5,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 0, 0),
                        new PuzzleAssignment.CheckpointSpec(2, 4, 0),
                        new PuzzleAssignment.CheckpointSpec(3, 0, 1),
                        new PuzzleAssignment.CheckpointSpec(4, 4, 2),
                        new PuzzleAssignment.CheckpointSpec(5, 0, 3),
                        new PuzzleAssignment.CheckpointSpec(6, 4, 4)
                ),
                List.of(),
                "MEDIUM", "Vertical Cascade");

        // Index 5: PackagedPuzzles.LEVEL_52 (5x5, 6 checkpoints, 2 walls) - Double Barrier
        addDailyPoolEntry("w3_lvl52", 1, 5, 5, cells5x5,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 0, 0),
                        new PuzzleAssignment.CheckpointSpec(2, 0, 4),
                        new PuzzleAssignment.CheckpointSpec(3, 1, 0),
                        new PuzzleAssignment.CheckpointSpec(4, 2, 4),
                        new PuzzleAssignment.CheckpointSpec(5, 3, 0),
                        new PuzzleAssignment.CheckpointSpec(6, 4, 4)
                ),
                List.of(
                        new PuzzleAssignment.BlockedEdgeSpec(0, 1, 1, 1),
                        new PuzzleAssignment.BlockedEdgeSpec(2, 3, 3, 3)
                ),
                "CHALLENGING", "Double Barrier");

        // Index 6: PackagedPuzzles.LEVEL_3 (4x4, 6 checkpoints, 0 walls) - Ascent Route
        addDailyPoolEntry("w1_lvl3", 1, 4, 4, cells4x4,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 0, 0),
                        new PuzzleAssignment.CheckpointSpec(2, 0, 3),
                        new PuzzleAssignment.CheckpointSpec(3, 1, 3),
                        new PuzzleAssignment.CheckpointSpec(4, 1, 0),
                        new PuzzleAssignment.CheckpointSpec(5, 2, 3),
                        new PuzzleAssignment.CheckpointSpec(6, 3, 0)
                ),
                List.of(),
                "BEGINNER", "Ascent Route");

        // Index 7: PackagedPuzzles.LEVEL_23 (5x5, 6 checkpoints, 0 walls) - Spiral Sweep
        addDailyPoolEntry("w2_lvl23", 1, 5, 5, cells5x5,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 4, 0),
                        new PuzzleAssignment.CheckpointSpec(2, 4, 4),
                        new PuzzleAssignment.CheckpointSpec(3, 3, 0),
                        new PuzzleAssignment.CheckpointSpec(4, 2, 4),
                        new PuzzleAssignment.CheckpointSpec(5, 1, 0),
                        new PuzzleAssignment.CheckpointSpec(6, 0, 4)
                ),
                List.of(),
                "MEDIUM", "Spiral Sweep");

        // Index 8: PackagedPuzzles.LEVEL_53 (5x5, 6 checkpoints, 3 walls) - Triple Bastion
        addDailyPoolEntry("w3_lvl53", 1, 5, 5, cells5x5,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 0, 0),
                        new PuzzleAssignment.CheckpointSpec(2, 0, 4),
                        new PuzzleAssignment.CheckpointSpec(3, 1, 0),
                        new PuzzleAssignment.CheckpointSpec(4, 2, 4),
                        new PuzzleAssignment.CheckpointSpec(5, 3, 0),
                        new PuzzleAssignment.CheckpointSpec(6, 4, 4)
                ),
                List.of(
                        new PuzzleAssignment.BlockedEdgeSpec(0, 1, 1, 1),
                        new PuzzleAssignment.BlockedEdgeSpec(2, 3, 3, 3),
                        new PuzzleAssignment.BlockedEdgeSpec(1, 2, 2, 2)
                ),
                "HARD", "Triple Bastion");

        // Index 9: PackagedPuzzles.LEVEL_4 (4x4, 5 checkpoints, 0 walls) - Crosswind
        addDailyPoolEntry("w1_lvl4", 1, 4, 4, cells4x4,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 3, 0),
                        new PuzzleAssignment.CheckpointSpec(2, 0, 0),
                        new PuzzleAssignment.CheckpointSpec(3, 3, 1),
                        new PuzzleAssignment.CheckpointSpec(4, 0, 2),
                        new PuzzleAssignment.CheckpointSpec(5, 3, 3)
                ),
                List.of(),
                "BEGINNER", "Crosswind");

        // Index 10: PackagedPuzzles.LEVEL_5 (4x4, 5 checkpoints, 0 walls) - Diagonal Sweep
        addDailyPoolEntry("w1_lvl5", 1, 4, 4, cells4x4,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 3, 3),
                        new PuzzleAssignment.CheckpointSpec(2, 3, 0),
                        new PuzzleAssignment.CheckpointSpec(3, 2, 3),
                        new PuzzleAssignment.CheckpointSpec(4, 1, 0),
                        new PuzzleAssignment.CheckpointSpec(5, 0, 3)
                ),
                List.of(),
                "BEGINNER", "Diagonal Sweep");

        // Index 11: PUZZLE_6X6_SERPENTINE (6x6, 7 checkpoints, 0 walls) - Hex Grid Odyssey
        addDailyPoolEntry("daily_curated_6x6_01", 1, 6, 6, cells6x6,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 0, 0),
                        new PuzzleAssignment.CheckpointSpec(2, 0, 5),
                        new PuzzleAssignment.CheckpointSpec(3, 1, 0),
                        new PuzzleAssignment.CheckpointSpec(4, 2, 5),
                        new PuzzleAssignment.CheckpointSpec(5, 3, 0),
                        new PuzzleAssignment.CheckpointSpec(6, 4, 5),
                        new PuzzleAssignment.CheckpointSpec(7, 5, 0)
                ),
                List.of(),
                "EXPERT", "Hex Grid Odyssey");

        // Index 12: PackagedPuzzles.LEVEL_21 (5x5, 6 checkpoints, 0 walls) - Verdant Echo
        addDailyPoolEntry("w2_lvl21", 1, 5, 5, cells5x5,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 0, 0),
                        new PuzzleAssignment.CheckpointSpec(2, 0, 4),
                        new PuzzleAssignment.CheckpointSpec(3, 1, 0),
                        new PuzzleAssignment.CheckpointSpec(4, 2, 4),
                        new PuzzleAssignment.CheckpointSpec(5, 3, 0),
                        new PuzzleAssignment.CheckpointSpec(6, 4, 4)
                ),
                List.of(),
                "MEDIUM", "Verdant Echo");

        // Index 13: PackagedPuzzles.LEVEL_51 (5x5, 6 checkpoints, 1 wall) - Stone Fortress
        addDailyPoolEntry("w3_lvl51", 1, 5, 5, cells5x5,
                List.of(
                        new PuzzleAssignment.CheckpointSpec(1, 0, 0),
                        new PuzzleAssignment.CheckpointSpec(2, 0, 4),
                        new PuzzleAssignment.CheckpointSpec(3, 1, 0),
                        new PuzzleAssignment.CheckpointSpec(4, 2, 4),
                        new PuzzleAssignment.CheckpointSpec(5, 3, 0),
                        new PuzzleAssignment.CheckpointSpec(6, 4, 4)
                ),
                List.of(new PuzzleAssignment.BlockedEdgeSpec(0, 1, 1, 1)),
                "CHALLENGING", "Stone Fortress");
    }

    private void addDailyPoolEntry(
            String puzzleId,
            int puzzleVersion,
            int rows,
            int cols,
            List<String> requiredCells,
            List<PuzzleAssignment.CheckpointSpec> checkpoints,
            List<PuzzleAssignment.BlockedEdgeSpec> blockedEdges,
            String difficultyTier,
            String title
    ) {
        String fingerprint = computeCanonicalFingerprint(rows, cols, requiredCells, checkpoints, blockedEdges);
        dailyPool.add(new DailyPuzzleEntry(
                puzzleId,
                puzzleVersion,
                fingerprint,
                rows,
                cols,
                requiredCells,
                checkpoints,
                blockedEdges,
                difficultyTier,
                title
        ));
    }

    /**
     * Computes the exact canonical SHA-256 fingerprint matching Android's PuzzleFingerprint.
     */
    public static String computeCanonicalFingerprint(
            int rows,
            int cols,
            List<String> requiredCells,
            List<PuzzleAssignment.CheckpointSpec> checkpoints,
            List<PuzzleAssignment.BlockedEdgeSpec> blockedEdges
    ) {
        String dims = rows + "x" + cols;

        List<int[]> cells = new ArrayList<>();
        for (String c : requiredCells) {
            String[] parts = c.split(",");
            cells.add(new int[]{Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim())});
        }
        cells.sort(Comparator.<int[]>comparingInt(p -> p[0]).thenComparingInt(p -> p[1]));
        StringBuilder cellsSb = new StringBuilder();
        for (int i = 0; i < cells.size(); i++) {
            if (i > 0) cellsSb.append(";");
            cellsSb.append(cells.get(i)[0]).append(",").append(cells.get(i)[1]);
        }

        List<PuzzleAssignment.CheckpointSpec> cps = new ArrayList<>(checkpoints);
        cps.sort(Comparator.comparingInt(PuzzleAssignment.CheckpointSpec::number));
        StringBuilder cpsSb = new StringBuilder();
        for (int i = 0; i < cps.size(); i++) {
            if (i > 0) cpsSb.append(";");
            PuzzleAssignment.CheckpointSpec cp = cps.get(i);
            cpsSb.append(cp.number()).append("@").append(cp.row()).append(",").append(cp.col());
        }

        List<int[]> walls = new ArrayList<>();
        for (PuzzleAssignment.BlockedEdgeSpec be : blockedEdges) {
            int r1 = be.row1();
            int c1 = be.col1();
            int r2 = be.row2();
            int c2 = be.col2();
            if (r1 > r2 || (r1 == r2 && c1 > c2)) {
                int tr = r1; int tc = c1;
                r1 = r2; c1 = c2;
                r2 = tr; c2 = tc;
            }
            walls.add(new int[]{r1, c1, r2, c2});
        }
        walls.sort(Comparator.<int[]>comparingInt(w -> w[0])
                .thenComparingInt(w -> w[1])
                .thenComparingInt(w -> w[2])
                .thenComparingInt(w -> w[3]));
        StringBuilder wallsSb = new StringBuilder();
        for (int i = 0; i < walls.size(); i++) {
            if (i > 0) wallsSb.append(";");
            int[] w = walls.get(i);
            wallsSb.append(w[0]).append(",").append(w[1]).append("-").append(w[2]).append(",").append(w[3]);
        }

        String canonical = "DIM:" + dims + "|CELLS:" + cellsSb + "|CP:" + cpsSb + "|WALLS:" + wallsSb;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(canonical.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Resolves the canonical daily challenge for the given UTC dateKey.
     */
    public DailyChallengeDefinitionDto getOfficialChallenge(String dateKey) {
        String effectiveDateKey = normalizeDateKey(dateKey);
        DailyPuzzleEntry entry = selectCanonicalPuzzle(effectiveDateKey, DEFAULT_SCHEDULE_VERSION);

        long now = System.currentTimeMillis();
        long deadlineMs = calculateDayDeadline(effectiveDateKey);

        List<DailyCheckpointSpec> checkpoints = entry.checkpoints().stream()
                .map(cp -> new DailyCheckpointSpec(cp.number(), cp.row(), cp.col()))
                .toList();

        List<DailyBlockedEdgeSpec> blockedEdges = entry.blockedEdges().stream()
                .map(be -> new DailyBlockedEdgeSpec(be.row1(), be.col1(), be.row2(), be.col2()))
                .toList();

        String challengeId = "daily-" + effectiveDateKey + "-v" + CHALLENGE_VERSION;

        return new DailyChallengeDefinitionDto(
                challengeId,
                effectiveDateKey,
                CHALLENGE_VERSION,
                DEFAULT_SCHEDULE_VERSION,
                entry.puzzleId(),
                entry.puzzleVersion(),
                entry.fingerprint(),
                entry.rows(),
                entry.cols(),
                checkpoints,
                blockedEdges,
                entry.difficultyTier(),
                entry.title(),
                now,
                deadlineMs,
                true
        );
    }

    /**
     * Initiates an official server-authoritative competitive attempt.
     * Idempotent: If attempt already exists for this player & dateKey, returns existing attempt.
     */
    public DailyChallengeAttemptDto startOfficialAttempt(String playerId, String dateKey) {
        PlayerAccount account = playerAccountService.findById(playerId)
                .orElseThrow(() -> AuthException.unauthenticated("PLAYER_NOT_FOUND: Authenticated player account required"));

        String effectiveDateKey = normalizeDateKey(dateKey);
        String attemptKey = playerId + ":" + effectiveDateKey;

        // Check if player has already finalized today's challenge
        DailyChallengeResultDto existingResult = finalResultKeyToResult.get(attemptKey);
        if (existingResult != null) {
            throw AuthException.badRequest("ATTEMPT_ALREADY_FINALIZED: You have already completed today's official challenge.");
        }

        // Check if player has an active unexpired attempt
        String existingAttemptId = activeAttemptKeyToAttemptId.get(attemptKey);
        if (existingAttemptId != null) {
            DailyChallengeAttemptDto existingAttempt = attemptsByAttemptId.get(existingAttemptId);
            if (existingAttempt != null && existingAttempt.status() == DailyAttemptStatus.ACTIVE) {
                if (System.currentTimeMillis() < existingAttempt.expiresAt()) {
                    log.info("Returning existing active attemptId={} for playerId={} dateKey={}",
                            existingAttemptId, playerId, effectiveDateKey);
                    return existingAttempt;
                }
            }
        }

        DailyChallengeDefinitionDto challenge = getOfficialChallenge(effectiveDateKey);
        long now = System.currentTimeMillis();
        long expiresAt = now + ATTEMPT_TTL_MILLIS;

        String attemptId = "att_daily_" + UUID.randomUUID();
        DailyChallengeAttemptDto attempt = new DailyChallengeAttemptDto(
                attemptId,
                playerId,
                challenge.challengeId(),
                effectiveDateKey,
                challenge.puzzleFingerprint(),
                now,
                expiresAt,
                DailyAttemptStatus.ACTIVE
        );

        attemptsByAttemptId.put(attemptId, attempt);
        activeAttemptKeyToAttemptId.put(attemptKey, attemptId);

        log.info("Started official daily attemptId={} for playerId={} dateKey={}", attemptId, playerId, effectiveDateKey);
        return attempt;
    }

    /**
     * Retrieves the active official attempt for a player on a given date.
     */
    public DailyChallengeAttemptDto getActiveAttempt(String playerId, String dateKey) {
        String effectiveDateKey = normalizeDateKey(dateKey);
        String attemptKey = playerId + ":" + effectiveDateKey;
        String attemptId = activeAttemptKeyToAttemptId.get(attemptKey);
        if (attemptId == null) return null;

        DailyChallengeAttemptDto attempt = attemptsByAttemptId.get(attemptId);
        if (attempt != null && attempt.status() == DailyAttemptStatus.ACTIVE) {
            if (System.currentTimeMillis() < attempt.expiresAt()) {
                return attempt;
            }
        }
        return null;
    }

    /**
     * Authoritatively validates a completed puzzle path and finalizes the competitive daily result.
     */
    public DailyChallengeResultDto submitCompletion(String playerId, DailyCompletionClaimDto claim) {
        if (claim == null || claim.attemptId() == null || claim.attemptId().isBlank()) {
            throw AuthException.badRequest("MISSING_ATTEMPT_ID: Official attempt ID is required");
        }

        DailyChallengeAttemptDto attempt = attemptsByAttemptId.get(claim.attemptId());
        if (attempt == null) {
            throw AuthException.notFound("ATTEMPT_NOT_FOUND: Specified attempt was not found");
        }

        if (!attempt.playerId().equals(playerId)) {
            throw AuthException.forbidden("ATTEMPT_ACCESS_DENIED: Attempt belongs to a different player");
        }

        String attemptKey = playerId + ":" + attempt.dateKey();

        // Idempotency: Return existing finalized result if re-submitted
        if (attempt.status() == DailyAttemptStatus.FINALIZED) {
            DailyChallengeResultDto existingResult = finalResultKeyToResult.get(attemptKey);
            if (existingResult != null) {
                log.info("Returning cached finalized resultId={} for attemptId={}", existingResult.resultId(), attempt.attemptId());
                return existingResult;
            }
        }

        long now = System.currentTimeMillis();
        if (now > attempt.expiresAt()) {
            throw AuthException.badRequest("ATTEMPT_EXPIRED: The official 2-hour competitive attempt window has expired");
        }

        DailyChallengeDefinitionDto challenge = getOfficialChallenge(attempt.dateKey());
        if (!challenge.puzzleFingerprint().equals(claim.puzzleFingerprint())) {
            throw AuthException.badRequest("PUZZLE_MISMATCH: Submitted puzzle fingerprint does not match canonical daily challenge");
        }

        // Construct PuzzleAssignment for server-side dual-win validation
        DailyPuzzleEntry puzzleEntry = selectCanonicalPuzzle(attempt.dateKey(), DEFAULT_SCHEDULE_VERSION);
        PuzzleAssignment assignment = new PuzzleAssignment(
                puzzleEntry.puzzleId(),
                puzzleEntry.puzzleVersion(),
                puzzleEntry.fingerprint(),
                puzzleEntry.rows(),
                puzzleEntry.cols(),
                puzzleEntry.requiredCells(),
                puzzleEntry.checkpoints(),
                puzzleEntry.blockedEdges()
        );

        ValidationOutcome outcome = serverPuzzleValidator.validateSolution(assignment, claim.pathCoordinates());
        if (!outcome.isValid()) {
            log.warn("Server validation rejected daily solution for attemptId={}: {}", claim.attemptId(), outcome.getErrorMessage());
            throw AuthException.badRequest("INVALID_COMPLETION: " + outcome.getErrorMessage());
        }

        // Calculate authoritative solve duration: Receipt - StartedAt
        long solveTimeMs = Math.max(1000L, now - attempt.startedAt());

        // Update attempt to FINALIZED
        DailyChallengeAttemptDto finalizedAttempt = new DailyChallengeAttemptDto(
                attempt.attemptId(),
                attempt.playerId(),
                attempt.challengeId(),
                attempt.dateKey(),
                attempt.puzzleFingerprint(),
                attempt.startedAt(),
                attempt.expiresAt(),
                DailyAttemptStatus.FINALIZED
        );
        attemptsByAttemptId.put(attempt.attemptId(), finalizedAttempt);

        // Fetch player identity
        PlayerAccount account = playerAccountService.findById(playerId).orElse(null);
        String publicZynpathId = account != null ? account.publicZynpathId() : "ZYN-XXXX-0000";
        String displayName = account != null ? account.displayName() : "Pathfinder";

        String resultId = "res_daily_" + UUID.randomUUID();
        DailyChallengeResultDto result = new DailyChallengeResultDto(
                resultId,
                attempt.attemptId(),
                playerId,
                publicZynpathId,
                displayName,
                "avatar_compass",
                challenge.challengeId(),
                attempt.dateKey(),
                challenge.puzzleFingerprint(),
                solveTimeMs,
                now,
                DailyVerificationStatus.SERVER_VALIDATED,
                true,
                null // Rank dynamically computed on leaderboard retrieval
        );

        finalResultKeyToResult.put(attemptKey, result);
        resultsByResultId.put(resultId, result);

        resultsByDateKey.computeIfAbsent(attempt.dateKey(), k -> new CopyOnWriteArrayList<>()).add(result);
        sortedEligibleResultsByDate.remove(attempt.dateKey());

        log.info("Authoritative daily challenge completion finalized: resultId={} playerId={} solveTimeMs={}",
                resultId, playerId, solveTimeMs);
        return result;
    }

    /**
     * Retrieves the personal result for a player on a given date.
     */
    public DailyChallengeResultDto getPersonalResult(String playerId, String dateKey) {
        String effectiveDateKey = normalizeDateKey(dateKey);
        String key = playerId + ":" + effectiveDateKey;
        DailyChallengeResultDto result = finalResultKeyToResult.get(key);
        if (result == null) return null;

        // Compute live rank
        int rank = calculatePlayerRank(effectiveDateKey, result.resultId());
        return new DailyChallengeResultDto(
                result.resultId(),
                result.attemptId(),
                result.playerId(),
                result.publicZynpathId(),
                result.displayName(),
                result.avatarId(),
                result.challengeId(),
                result.dateKey(),
                result.puzzleFingerprint(),
                result.solveTimeMs(),
                result.completedAt(),
                result.verificationStatus(),
                result.isLeaderboardEligible(),
                rank > 0 ? rank : null
        );
    }

    /**
     * Retrieves the paginated daily leaderboard for an official challenge date.
     */
    public DailyLeaderboardResponse getDailyLeaderboard(String dateKey, int page, int pageSize, String viewingPlayerId) {
        String effectiveDateKey = normalizeDateKey(dateKey);
        DailyChallengeDefinitionDto challenge = getOfficialChallenge(effectiveDateKey);

        // Section 60 & 63: Use cached pre-sorted eligible records for performance
        List<DailyChallengeResultDto> eligibleResults = getSortedEligibleResults(effectiveDateKey);

        int totalEntries = eligibleResults.size();
        int safePage = Math.max(0, page);
        int safePageSize = Math.min(50, Math.max(1, pageSize));

        int fromIndex = Math.min(safePage * safePageSize, totalEntries);
        int toIndex = Math.min(fromIndex + safePageSize, totalEntries);

        List<DailyLeaderboardEntryDto> pagedEntries = new ArrayList<>();
        int currentRank = 1;
        for (int i = 0; i < eligibleResults.size(); i++) {
            DailyChallengeResultDto res = eligibleResults.get(i);
            if (i > 0 && res.solveTimeMs() > eligibleResults.get(i - 1).solveTimeMs()) {
                currentRank = i + 1;
            }
            if (i >= fromIndex && i < toIndex) {
                pagedEntries.add(new DailyLeaderboardEntryDto(
                        currentRank,
                        res.publicZynpathId(),
                        res.displayName(),
                        res.avatarId(),
                        res.solveTimeMs(),
                        res.completedAt(),
                        res.verificationStatus()
                ));
            }
        }

        // Find viewing player's entry if eligible
        DailyLeaderboardEntryDto playerEntry = null;
        if (viewingPlayerId != null && !viewingPlayerId.isBlank()) {
            int pRank = 1;
            for (int i = 0; i < eligibleResults.size(); i++) {
                DailyChallengeResultDto res = eligibleResults.get(i);
                if (i > 0 && res.solveTimeMs() > eligibleResults.get(i - 1).solveTimeMs()) {
                    pRank = i + 1;
                }
                if (res.playerId().equals(viewingPlayerId)) {
                    playerEntry = new DailyLeaderboardEntryDto(
                            pRank,
                            res.publicZynpathId(),
                            res.displayName(),
                            res.avatarId(),
                            res.solveTimeMs(),
                            res.completedAt(),
                            res.verificationStatus()
                    );
                    break;
                }
            }
        }

        return new DailyLeaderboardResponse(
                challenge.challengeId(),
                effectiveDateKey,
                challenge.puzzleFingerprint(),
                pagedEntries,
                playerEntry,
                totalEntries,
                safePage,
                safePageSize
        );
    }

    /**
     * Synchronizes local offline provisional completion records.
     * Offline completions are preserved for streaks and history, but remain marked PROVISIONAL
     * and are NOT placed into the authoritative timed competitive leaderboard.
     */
    public DailyChallengeResultDto syncProvisional(String playerId, DailyProvisionalSyncRequest request) {
        if (request == null) throw AuthException.badRequest("REQUEST_NULL");

        String effectiveDateKey = normalizeDateKey(request.dateKey());
        String attemptKey = playerId + ":" + effectiveDateKey;

        // If player already has a SERVER_VALIDATED result, do not overwrite
        DailyChallengeResultDto existing = finalResultKeyToResult.get(attemptKey);
        if (existing != null && existing.isLeaderboardEligible()) {
            return existing;
        }

        PlayerAccount account = playerAccountService.findById(playerId).orElse(null);
        String publicZynpathId = account != null ? account.publicZynpathId() : "ZYN-XXXX-0000";
        String displayName = account != null ? account.displayName() : "Pathfinder";

        String resultId = "res_prov_" + UUID.randomUUID();
        DailyChallengeResultDto provResult = new DailyChallengeResultDto(
                resultId,
                "prov_" + UUID.randomUUID(),
                playerId,
                publicZynpathId,
                displayName,
                "avatar_compass",
                request.challengeId(),
                effectiveDateKey,
                request.puzzleFingerprint(),
                request.solveTimeMs(),
                request.completedAt(),
                DailyVerificationStatus.PROVISIONAL,
                false, // Ineligible for timed leaderboard
                null
        );

        finalResultKeyToResult.put(attemptKey, provResult);
        resultsByResultId.put(resultId, provResult);

        log.info("Recorded provisional offline daily completion: playerId={} dateKey={}", playerId, effectiveDateKey);
        return provResult;
    }

    private List<DailyChallengeResultDto> getSortedEligibleResults(String dateKey) {
        return sortedEligibleResultsByDate.computeIfAbsent(dateKey, key -> {
            List<DailyChallengeResultDto> all = resultsByDateKey.getOrDefault(key, Collections.emptyList());
            return all.stream()
                    .filter(DailyChallengeResultDto::isLeaderboardEligible)
                    .sorted(Comparator.comparingLong(DailyChallengeResultDto::solveTimeMs)
                            .thenComparingLong(DailyChallengeResultDto::completedAt)
                            .thenComparing(DailyChallengeResultDto::publicZynpathId))
                    .toList();
        });
    }

    private int calculatePlayerRank(String dateKey, String targetResultId) {
        List<DailyChallengeResultDto> eligible = getSortedEligibleResults(dateKey);

        int currentRank = 1;
        for (int i = 0; i < eligible.size(); i++) {
            DailyChallengeResultDto res = eligible.get(i);
            if (i > 0 && res.solveTimeMs() > eligible.get(i - 1).solveTimeMs()) {
                currentRank = i + 1;
            }
            if (res.resultId().equals(targetResultId)) {
                return currentRank;
            }
        }
        return -1;
    }

    private DailyPuzzleEntry selectCanonicalPuzzle(String dateKey, String scheduleVersion) {
        int index = computeDeterministicIndex(dateKey, scheduleVersion, dailyPool.size());
        return dailyPool.get(index);
    }

    private int computeDeterministicIndex(String dateKey, String scheduleVersion, int poolSize) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] input = (scheduleVersion + ":" + dateKey).getBytes(StandardCharsets.UTF_8);
            byte[] hash = digest.digest(input);
            long intValue = ByteBuffer.wrap(hash).getInt() & 0xFFFFFFFFL;
            return (int) (intValue % (long) poolSize);
        } catch (NoSuchAlgorithmException e) {
            return Math.abs(dateKey.hashCode()) % poolSize;
        }
    }

    private String normalizeDateKey(String dateKey) {
        if (dateKey == null || dateKey.isBlank()) {
            return LocalDate.now(ZoneOffset.UTC).toString();
        }
        if (!dateKey.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw AuthException.badRequest("INVALID_DATE_FORMAT: Expected YYYY-MM-DD format (e.g. 2026-09-26)");
        }
        return dateKey;
    }

    private long calculateDayDeadline(String dateKey) {
        try {
            LocalDate day = LocalDate.parse(dateKey);
            return day.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        } catch (Exception e) {
            return System.currentTimeMillis() + 86_400_000L;
        }
    }

    /**
     * Retrieve all server-verified daily challenge results for a given player.
     */
    public List<DailyChallengeResultDto> getPlayerDailyResults(String playerId) {
        if (playerId == null || playerId.isBlank()) {
            return Collections.emptyList();
        }
        return finalResultKeyToResult.values().stream()
                .filter(r -> playerId.equals(r.playerId()))
                .sorted(Comparator.comparing(DailyChallengeResultDto::dateKey).reversed())
                .toList();
    }
}
