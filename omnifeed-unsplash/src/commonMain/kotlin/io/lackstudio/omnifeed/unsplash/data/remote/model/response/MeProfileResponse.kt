package io.lackstudio.omnifeed.unsplash.data.remote.model.response

import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.UserProfileDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.BadgeDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.MetaDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.PreviewPhotoDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.ProfileImageDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.SocialDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.TagsDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.UserLinksDto
import io.lackstudio.omnifeed.unsplash.utils.constants.ApiKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeProfileResponse(
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
    override val photos: List<PreviewPhotoDto> = emptyList(),
    override val badge: BadgeDto? = null,
    override val tags: TagsDto,
    @SerialName(ApiKeys.User.ALLOW_MESSAGES) override val allowMessages: Boolean,
    @SerialName(ApiKeys.User.NUMERIC_ID) override val numericId: Long,
    override val downloads: Long,
    override val meta: MetaDto,
    val uid: String = "",
    val confirmed: Boolean = false,
    val teams: List<String> = emptyList(),
    // read_user permission
    val email: String? = null,
    @SerialName(ApiKeys.User.UPLOADS_REMAINING) val uploadsRemaining: Int? = null,
    @SerialName(ApiKeys.User.UNLIMITED_UPLOADS) val unlimitedUploads: Boolean? = null,
    @SerialName(ApiKeys.User.DMCA_VERIFICATION) val dmcaVerification: String? = null,
    @SerialName(ApiKeys.User.UNREAD_IN_APP_NOTIFICATIONS) val unreadInAppNotifications: Boolean? = null,
    @SerialName(ApiKeys.User.UNREAD_HIGHLIGHT_NOTIFICATIONS) val unreadHighlightNotifications: Boolean? = null,
) : UserProfileDto
