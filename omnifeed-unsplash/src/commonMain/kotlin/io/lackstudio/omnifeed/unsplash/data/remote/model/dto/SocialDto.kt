package io.lackstudio.omnifeed.unsplash.data.remote.model.dto

import io.lackstudio.omnifeed.unsplash.utils.constants.ApiKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SocialDto(
    @SerialName(ApiKeys.Social.INSTAGRAM_USERNAME) val instagramUsername: String? = null,
    @SerialName(ApiKeys.Social.PORTFOLIO_URL) val portfolioUrl: String? = null,
    @SerialName(ApiKeys.Social.TWITTER_USERNAME) val twitterUsername: String? = null,
    @SerialName(ApiKeys.Social.PAYPAL_EMAIL) val paypalEmail: String? = null
)
