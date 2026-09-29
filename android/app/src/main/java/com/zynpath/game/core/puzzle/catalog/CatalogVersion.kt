package com.zynpath.game.core.puzzle.catalog

/**
 * Authoritative version constants for Zynpath catalog and asset serialization schemas.
 *
 * Implements Prompt 11 Section 18:
 * Strictly separates catalog version, puzzle version, generator version, and asset schema version.
 */
object CatalogVersion {
    /** Version of the offline catalog specification and manifest hierarchy. */
    const val CATALOG_VERSION = "1.0.0"

    /** Version of the catalog manifest JSON schema. */
    const val MANIFEST_SCHEMA_VERSION = "1.0.0"

    /** Version of the individual puzzle asset JSON schema. */
    const val ASSET_SCHEMA_VERSION = "1.0.0"

    /** Standard puzzle definition model version. */
    const val DEFAULT_PUZZLE_VERSION = 1
}
