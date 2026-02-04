package io.lackstudio.omnifeed.unsplash.data.model.scheme

import io.lackstudio.omnifeed.unsplash.utils.constants.ApiKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SponsorshipScheme(
    @SerialName(ApiKeys.Sponsorship.IMPRESSION_URLS) val impressionUrls: List<String>,
    val tagline: String,
    @SerialName(ApiKeys.Sponsorship.TAGLINE_URL) val taglineUrl: String,
    val sponsor: ParticipantScheme
)
