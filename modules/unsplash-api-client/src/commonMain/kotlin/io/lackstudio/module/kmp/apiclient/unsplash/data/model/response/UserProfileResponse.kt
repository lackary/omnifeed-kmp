package io.lackstudio.module.kmp.apiclient.unsplash.data.model.response

import io.lackstudio.module.kmp.apiclient.unsplash.data.model.dto.UserDto
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.dto.UserProfileDto
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.BadgeScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.MetaScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.PreviewPhotoScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.ProfileImageScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.SocialScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.TagsScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.UserLinksScheme
import io.lackstudio.module.kmp.apiclient.unsplash.utils.constants.ApiKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponse(
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
    override val links: UserLinksScheme,
    @SerialName(ApiKeys.Common.PROFILE_IMAGE) override val profileImage: ProfileImageScheme,
    @SerialName(ApiKeys.Social.INSTAGRAM_USERNAME) override val instagramUsername: String? = null,
    @SerialName(ApiKeys.Statistics.TOTAL_COLLECTIONS) override val totalCollections: Long,
    @SerialName(ApiKeys.Statistics.TOTAL_LIKES) override val totalLikes: Long,
    @SerialName(ApiKeys.Statistics.TOTAL_PHOTOS) override val totalPhotos: Long,
    @SerialName(ApiKeys.Statistics.TOTAL_PROMOTED_PHOTOS) override val totalPromotedPhotos: Long,
    @SerialName(ApiKeys.Statistics.TOTAL_ILLUSTRATIONS) override val totalIllustrations: Long,
    @SerialName(ApiKeys.Statistics.TOTAL_PROMOTED_ILLUSTRATIONS) override val totalPromotedIllustrations: Long,
    @SerialName(ApiKeys.User.ACCEPTED_TOS) override val acceptedTos: Boolean,
    @SerialName(ApiKeys.Common.FOR_HIRE) override val forHire: Boolean,
    override val social: SocialScheme,
    override val photos: List<PreviewPhotoScheme>,
    override val badge: BadgeScheme? = null,
    override val tags: TagsScheme? = null,
    @SerialName(ApiKeys.User.ALLOW_MESSAGES) override val allowMessages: Boolean? = null,
    @SerialName(ApiKeys.User.NUMERIC_ID) override val numericId: Long? = null,
    override val downloads: Long? = null,
    override val meta: MetaScheme? = null,
) : UserDto, UserProfileDto
