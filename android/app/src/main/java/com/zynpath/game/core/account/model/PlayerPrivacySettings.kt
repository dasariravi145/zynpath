package com.zynpath.game.core.account.model

data class PlayerPrivacySettings(
    val profileVisibility: ProfileVisibility = ProfileVisibility.PUBLIC,
    val allowZynpathIdSearch: Boolean = true,
    val allowFriendRequests: Boolean = true
)
