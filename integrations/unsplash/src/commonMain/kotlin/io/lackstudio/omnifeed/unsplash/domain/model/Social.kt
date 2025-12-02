package io.lackstudio.omnifeed.unsplash.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Social(
    val instagramUsername: String? = null,
    val portfolioUrl: String? = null,
    val twitterUsername: String? = null,
    val paypalEmail: String? = null
)
