    package io.lackstudio.omnifeed.unsplash.mapper

import io.lackstudio.omnifeed.unsplash.data.model.scheme.BadgeScheme
import io.lackstudio.omnifeed.unsplash.data.model.scheme.PreviewPhotoScheme
import io.lackstudio.omnifeed.unsplash.data.model.scheme.ProfileImageScheme
import io.lackstudio.omnifeed.unsplash.data.model.scheme.SocialScheme
import io.lackstudio.omnifeed.unsplash.data.model.response.MeProfileResponse
import io.lackstudio.omnifeed.unsplash.data.model.response.UserProfileResponse
import io.lackstudio.omnifeed.unsplash.data.model.scheme.TagsScheme
import io.lackstudio.omnifeed.unsplash.domain.model.Badge
import io.lackstudio.omnifeed.unsplash.domain.model.Me
import io.lackstudio.omnifeed.unsplash.domain.model.PreviewPhoto
import io.lackstudio.omnifeed.unsplash.domain.model.ProfileImage
import io.lackstudio.omnifeed.unsplash.domain.model.Social
import io.lackstudio.omnifeed.unsplash.domain.model.Tags
import io.lackstudio.omnifeed.unsplash.domain.model.UserProfile

fun MeProfileResponse.toMe(): Me {
    return Me(
        id = this.id,
        updatedAt = this.updatedAt,
        username = this.username,
        name = this.name,
        firstName = this.firstName,
        lastName = this.lastName,
        twitterUsername = this.twitterUsername,
        portfolioUrl = this.portfolioUrl,
        bio = this.bio,
        location = this.location,
        links = this.links.toUserLinks(),
        profileImage = this.profileImage.toProfileImage(),
        instagramUsername = this.instagramUsername,
        totalCollections = this.totalCollections,
        totalLikes = this.totalLikes,
        totalPhotos = this.totalPhotos,
        totalPromotedPhotos = this.totalPromotedPhotos,
        totalIllustrations = this.totalIllustrations,
        totalPromotedIllustrations = this.totalPromotedIllustrations,
        acceptedTos = this.acceptedTos,
        forHire = this.forHire,
        social = this.social.toSocial(),
        photos = this.photos.map { previewPhotoScheme -> previewPhotoScheme.toPhotoDetail() },
        badge = this.badge?.toBadge(),
        tags = this.tags.toTags(),
        allowMessages = this.allowMessages,
        numericId = this.numericId,
        downloads = this.downloads,
        meta = this.meta.toMeta(),
        uid = this.uid,
        confirmed = this.confirmed
    )
}

fun UserProfileResponse.toUserProfile(): UserProfile {
    return UserProfile(
        id = this.id,
        updatedAt = this.updatedAt,
        username = this.username,
        name = this.name,
        firstName = this.firstName,
        lastName = this.lastName,
        twitterUsername = this.twitterUsername,
        portfolioUrl = this.portfolioUrl,
        bio = this.bio,
        location = this.location,
        links = this.links.toUserLinks(),
        profileImage = this.profileImage.toProfileImage(),
        instagramUsername = this.instagramUsername,
        totalCollections = this.totalCollections,
        totalLikes = this.totalLikes,
        totalPhotos = this.totalPhotos,
        totalPromotedPhotos = this.totalPromotedPhotos,
        totalIllustrations = this.totalIllustrations,
        totalPromotedIllustrations = this.totalPromotedIllustrations,
        acceptedTos = this.acceptedTos,
        forHire = this.forHire,
        social = this.social.toSocial(),
        photos = this.photos.map { previewPhotoScheme -> previewPhotoScheme.toPhotoDetail() },
        badge = this.badge?.toBadge(),
        tags = this.tags?.toTags(),
        allowMessages = this.allowMessages,
        numericId = this.numericId,
        downloads = this.downloads,
        meta = this.meta?.toMeta()
    )
}

fun ProfileImageScheme.toProfileImage(): ProfileImage {
    return ProfileImage(
        large = this.large,
        medium = this.medium,
        small = this.small
    )
}

fun BadgeScheme.toBadge(): Badge {
    return Badge(
        title = this.title,
        primary = this.primary,
        slug = this.slug,
        link = this.link
    )
}

fun PreviewPhotoScheme.toPhotoDetail(): PreviewPhoto {
    return PreviewPhoto(
        id = this.id,
        slug = this.slug,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        blurHash = this.blurHash,
        assetType = this.assetType,
        urls = this.urls.toPhotoUrls()
    )
}



fun SocialScheme.toSocial(): Social {
    return Social(
        instagramUsername = this.instagramUsername,
        portfolioUrl = this.portfolioUrl,
        twitterUsername = this.twitterUsername,
        paypalEmail = this.paypalEmail
    )
}

fun TagsScheme.toTags(): Tags {
    return Tags(
        custom = this.custom.map { tagScheme -> tagScheme.toTag() },
        aggregated = this.aggregated.map { tagScheme -> tagScheme.toTag() }
    )
}
