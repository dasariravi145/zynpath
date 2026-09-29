package com.zynpath.game.core.social.model

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Utility for generating, validating, and sharing player invitation links.
 *
 * Implements Prompt 19 Sections 24-27:
 * - Versioned invitation URL with public Zynpath ID only.
 * - Omits internal database IDs, tokens, or personal info.
 * - Validates incoming deep link URIs and formats.
 * - Integrates with Android Sharesheet.
 */
object InvitationLinkHelper {

    private const val DEEP_LINK_SCHEME = "zynpath"
    private const val DEEP_LINK_HOST = "invite"
    private const val WEB_INVITE_HOST = "zynpath.com"
    private const val WEB_INVITE_PATH = "/invite"
    private const val PARAM_ID = "id"

    // Valid public Zynpath ID format: starts with ZYN-, followed by alphanumeric segments
    private val PUBLIC_ID_REGEX = Regex("^ZYN-[A-Z0-9]{4,12}(-[A-Z0-9]{4,12})?$", RegexOption.IGNORE_CASE)

    /**
     * Generates a shareable HTTPS web link.
     */
    fun createWebInviteLink(publicZynpathId: String): String {
        return "https://$WEB_INVITE_HOST$WEB_INVITE_PATH?$PARAM_ID=${Uri.encode(publicZynpathId)}"
    }

    /**
     * Generates an internal deep link URI.
     */
    fun createDeepLinkUri(publicZynpathId: String): Uri {
        return Uri.parse("$DEEP_LINK_SCHEME://$DEEP_LINK_HOST?$PARAM_ID=${Uri.encode(publicZynpathId)}")
    }

    /**
     * Validates whether a public Zynpath ID has valid syntax.
     */
    fun isValidPublicId(publicId: String?): Boolean {
        if (publicId.isNullOrBlank()) return false
        return PUBLIC_ID_REGEX.matches(publicId.trim())
    }

    /**
     * Parses and extracts a public Zynpath ID from an incoming deep link or web URI.
     * Returns null if the URI is malformed or does not contain a valid public ID.
     */
    fun parsePublicIdFromUri(uri: Uri?): String? {
        if (uri == null) return null

        val isDeepLink = DEEP_LINK_SCHEME.equals(uri.scheme, ignoreCase = true) &&
                DEEP_LINK_HOST.equals(uri.host, ignoreCase = true)

        val isWebLink = ("https".equals(uri.scheme, ignoreCase = true) || "http".equals(uri.scheme, ignoreCase = true)) &&
                WEB_INVITE_HOST.equals(uri.host, ignoreCase = true) &&
                WEB_INVITE_PATH.equals(uri.path, ignoreCase = true)

        if (!isDeepLink && !isWebLink) {
            return null
        }

        val publicId = uri.getQueryParameter(PARAM_ID)
        return if (isValidPublicId(publicId)) publicId?.trim() else null
    }

    /**
     * Creates an Android Sharesheet Intent for sharing the player's invitation link.
     */
    fun createShareIntent(context: Context, publicZynpathId: String, displayName: String): Intent {
        val inviteLink = createWebInviteLink(publicZynpathId)
        val shareText = "Join me on Zynpath: Number Path Puzzle! Add me using my Zynpath ID: $publicZynpathId or tap: $inviteLink"

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Play Zynpath with $displayName")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        return Intent.createChooser(sendIntent, "Invite friends to Zynpath")
    }
}
