package io.lackstudio.omnifeed.unsplash.data.model.response

import io.lackstudio.omnifeed.unsplash.data.model.scheme.LinksScheme
import io.lackstudio.omnifeed.unsplash.data.model.scheme.PhotoScheme
import io.lackstudio.omnifeed.unsplash.data.model.scheme.PreviewPhotoScheme
import io.lackstudio.omnifeed.unsplash.data.model.scheme.ParticipantScheme
import io.lackstudio.omnifeed.unsplash.utils.constants.ApiKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TopicResponse(
    val id: String,
    val slug: String,
    val title: String,
    val description: String,
    @SerialName(ApiKeys.Common.PUBLISHED_AT) val publishedAt: String,
    @SerialName(ApiKeys.Common.UPDATED_AT) val updatedAt: String? = null,
    @SerialName(ApiKeys.Common.STARTS_AT) val startsAt: String,
    @SerialName(ApiKeys.Common.ENDS_AT) val endsAt: String? = null,
//    @SerialName("only_submissions_after") val onlySubmissionsAfter: Any?,
    val visibility: String,
    val featured: Boolean,
    @SerialName(ApiKeys.Statistics.TOTAL_PHOTOS) val totalPhotos: Int,
//    @SerialName("current_user_contributions") val currentUserContributions: List<Any?>,
//    @SerialName("total_current_user_submissions") val totalCurrentUserSubmissions: Any?,
    val links: LinksScheme,
    @SerialName(ApiKeys.Common.MEDIA_TYPES) val mediaTypes: List<String>,
    val status: String,
    val owners: List<ParticipantScheme>,
    @SerialName(ApiKeys.Topic.TOP_CONTRIBUTORS) val topContributors: List<ParticipantScheme>? = emptyList(),
    @SerialName(ApiKeys.Common.COVER_PHOTO) val coverPhoto: PhotoScheme,
    @SerialName(ApiKeys.Common.PREVIEW_PHOTOS) val previewPhotos: List<PreviewPhotoScheme>
)
