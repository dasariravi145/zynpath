package com.zynpath.game.core.puzzle.generator

/**
 * Deterministic seed derivation engine ensuring reproducible, stable level identity.
 *
 * Implements Prompt 26 Task 8:
 * - Maps any level ID [1..300] to a fixed, reproducible 64-bit seed.
 * - Prevents random level reshuffling upon screen recomposition or app restart.
 * - Uses SplitMix64 mixing to guarantee uniform avalanche distribution across sequential level numbers.
 */
object LevelSeedGenerator {

    private const val DEFAULT_CATALOG_SALT = 0x594E504154484CL // "ZYNPATHL"
    private val MULT_1 = 0x9E3779B97F4A7C15uL.toLong()
    private val MULT_2 = 0xBF58476D1CE4E5B9uL.toLong()
    private val MULT_3 = 0x94D049BB133111EBuL.toLong()
    private const val ATTEMPT_MULT = 0x517CC1B727220A95L

    /**
     * Computes a stable, high-entropy 64-bit seed for a specific level.
     *
     * @param levelId Level number in 1..300.
     * @param salt Optional version salt for catalog revamps.
     * @return Deterministic 64-bit seed.
     */
    fun computeSeed(levelId: Int, salt: Long = DEFAULT_CATALOG_SALT): Long {
        require(levelId in 1..300) { "levelId must be in 1..300 (got $levelId)" }
        var z = (levelId.toLong() + 1000L) * MULT_1 xor salt
        z = (z xor (z ushr 30)) * MULT_2
        z = (z xor (z ushr 27)) * MULT_3
        return z xor (z ushr 31)
    }

    /**
     * Derives a secondary retry seed if an initial candidate is rejected by solver or similarity gates.
     */
    fun computeCandidateSeed(levelId: Int, attemptIndex: Int, salt: Long = DEFAULT_CATALOG_SALT): Long {
        val base = computeSeed(levelId, salt)
        var z = base + (attemptIndex.toLong() * ATTEMPT_MULT)
        z = (z xor (z ushr 30)) * MULT_2
        z = (z xor (z ushr 27)) * MULT_3
        return z xor (z ushr 31)
    }
}
