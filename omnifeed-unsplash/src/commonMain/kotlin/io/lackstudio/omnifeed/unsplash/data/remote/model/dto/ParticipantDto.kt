package io.lackstudio.omnifeed.unsplash.data.remote.model.dto

import io.lackstudio.omnifeed.unsplash.utils.constants.ApiKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParticipantDto(
    override val id: String,
    @SerialName(ApiKeys.Common.UPDATED_AT) override val updatedAt: String,
    override val username: String,
    override val name: String,
    @SerialName(ApiKeys.User.FIRST_NAME) override val firstName: String,
    @SerialName(ApiKeys.User.LAST_NAME) override val lastName: String? = null,
    @SerialName(ApiKeys.Social.TWITTER_USERNAME) override val twitterUsername: String? = null,
    @SerialName(ApiKeys.Social.PORTFOLIO_URL) override val portfolioUrl: String? = null,
    override val bio: String? = null,
    override val location: String? = null,
    override val links: UserLinksDto,
    @SerialName(ApiKeys.Common.PROFILE_IMAGE) override val profileImage: ProfileImageDto,
    @SerialName(ApiKeys.Social.INSTAGRAM_USERNAME) override val instagramUsername: String? = null,
    @SerialName(ApiKeys.Statistics.TOTAL_COLLECTIONS) override val totalCollections: Long = 0,
    @SerialName(ApiKeys.Statistics.TOTAL_LIKES) override val totalLikes: Long = 0,
    @SerialName(ApiKeys.Statistics.TOTAL_PHOTOS) override val totalPhotos: Long = 0,
    @SerialName(ApiKeys.Statistics.TOTAL_FREE_PHOTOS) override val totalFreePhotos: Long = 0,
    @SerialName(ApiKeys.Statistics.TOTAL_PROMOTED_PHOTOS) override val totalPromotedPhotos: Long = 0,
    @SerialName(ApiKeys.Statistics.TOTAL_ILLUSTRATIONS) override val totalIllustrations: Long = 0,
    @SerialName(ApiKeys.Statistics.TOTAL_FREE_ILLUSTRATIONS) override val totalFreeIllustrations: Long = 0,
    @SerialName(ApiKeys.Statistics.TOTAL_PROMOTED_ILLUSTRATIONS) override val totalPromotedIllustrations: Long = 0,
    @SerialName(ApiKeys.User.ACCEPTED_TOS) override val acceptedTos: Boolean = false,
    @SerialName(ApiKeys.Common.FOR_HIRE) override val forHire: Boolean = false,
    override val social: SocialDto,
) : UserDto
