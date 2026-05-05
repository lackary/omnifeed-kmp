package io.lackstudio.omnifeed.unsplash.mapper

import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.AlternativeSlugsDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.BreadcrumbDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.CategoryDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.CurrentUserCollectionDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.ExifDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.LocationDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.ParticipantDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.PhotoDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.PhotoLinksDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.PhotoUrlsDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.PhotoUserDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.PositionDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.RelatedCollectionDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.SponsorshipDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.TopicSimpleDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.TopicSubmissionsDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.UserLinksDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.response.PhotoDetailResponse
import io.lackstudio.omnifeed.unsplash.domain.model.AlternativeSlugs
import io.lackstudio.omnifeed.unsplash.domain.model.Breadcrumb
import io.lackstudio.omnifeed.unsplash.domain.model.Category
import io.lackstudio.omnifeed.unsplash.domain.model.CurrentUserCollection
import io.lackstudio.omnifeed.unsplash.domain.model.Exif
import io.lackstudio.omnifeed.unsplash.domain.model.Location
import io.lackstudio.omnifeed.unsplash.domain.model.Photo
import io.lackstudio.omnifeed.unsplash.domain.model.PhotoDetail
import io.lackstudio.omnifeed.unsplash.domain.model.PhotoLinks
import io.lackstudio.omnifeed.unsplash.domain.model.PhotoUrls
import io.lackstudio.omnifeed.unsplash.domain.model.PhotoUser
import io.lackstudio.omnifeed.unsplash.domain.model.Position
import io.lackstudio.omnifeed.unsplash.domain.model.RelatedCollection
import io.lackstudio.omnifeed.unsplash.domain.model.Sponsor
import io.lackstudio.omnifeed.unsplash.domain.model.Sponsorship
import io.lackstudio.omnifeed.unsplash.domain.model.TopicSimple
import io.lackstudio.omnifeed.unsplash.domain.model.TopicSubmissions
import io.lackstudio.omnifeed.unsplash.domain.model.UserLinks

fun PhotoDto.toPhotoDetail(): Photo {
    return Photo(
        altDescription = this.altDescription,
        alternativeSlugs = this.alternativeSlugs.toAlternativeSlugs(),
        assetType = this.assetType,
        blurHash = this.blurHash,
        breadcrumbs = this.breadcrumbs?.map { it.toBreadcrumb() },
        color = this.color,
        createdAt = this.createdAt,
        currentUserCollections = this.currentUserCollections.map { it.toCurrentUserCollection() },
        description = this.description,
        height = height,
        id = this.id,
        likedByUser = this.likedByUser,
        likes = this.likes,
        links = this.links.toPhotoLinks(),
        promotedAt = this.promotedAt,
        slug = this.slug,
        sponsorship = this.sponsorship?.toSponsorship(),
        topicSubmissions = this.topicSubmissions.toTopicSubmissions(),
        updatedAt = this.updatedAt,
        urls = this.urls.toPhotoUrls(),
        user = this.user.toPhotoUser(),
        width = this.width,
    )
}

fun PhotoDetailResponse.toPhotoDetail(): PhotoDetail {
    return PhotoDetail(
        id = this.id,
        slug = this.slug,
        alternativeSlugs = this.alternativeSlugs.toAlternativeSlugs(),
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        promotedAt = this.promotedAt,
        width = this.width,
        height = this.height,
        color = this.color,
        blurHash = this.blurHash,
        description = this.description,
        altDescription = this.altDescription,
        breadcrumbs = this.breadcrumbs?.map { it.toBreadcrumb() },
        urls = this.urls.toPhotoUrls(),
        links = this.links.toPhotoLinks(),
        likes = this.likes,
        likedByUser = this.likedByUser,
        bookmarked = this.bookmarked,
        currentUserCollections = this.currentUserCollections?.map { it.toCurrentUserCollection() },
        sponsorship = this.sponsorship?.toSponsorship(),
        topicSubmissions = this.topicSubmissions.toTopicSubmissions(),
        assetType = this.assetType,
        showOnProfile = this.showOnProfile,
        user = this.user.toPhotoUser(),
        exif = this.exif.toExif(),
        location = this.location.toLocation(),
        meta = this.meta.toMeta(),
        publicDomain = this.publicDomain,
        tags = this.tags.map { it.toTag() },
        views = this.views,
        downloads = this.downloads,
        topics = this.topics.map { it.toTopicSimple() },
        relatedCollections = this.relatedCollections?.toRelatedCollection()
    )
}

fun AlternativeSlugsDto.toAlternativeSlugs(): AlternativeSlugs {
    return AlternativeSlugs(
        german = this.german,
        english = this.english,
        spanish = this.spanish,
        french = this.french,
        indonesian = this.indonesian,
        italian = this.italian,
        japanese = this.japanese,
        korean = this.korean,
        portuguese = this.portuguese
    )
}

fun PhotoLinksDto.toPhotoLinks(): PhotoLinks {
    return PhotoLinks(
        self = this.self,
        html = this.html,
        download = this.download,
        downloadLocation = this.downloadLocation
    )
}

fun SponsorshipDto.toSponsorship(): Sponsorship {
    return Sponsorship(
        impressionUrls = this.impressionUrls,
        sponsor = this.sponsor.toSponsor(),
        tagline = this.tagline,
        taglineUrl = this.taglineUrl
    )
}

fun PhotoUrlsDto.toPhotoUrls(): PhotoUrls {
    return PhotoUrls(
        raw = this.raw,
        full = this.full,
        regular = this.regular,
        small = this.small,
        thumb = this.thumb,
        smallS3 = smallS3
    )
}

fun UserLinksDto.toUserLinks(): UserLinks {
    return UserLinks(
        html = this.html,
        likes = this.photos,
        photos = this.photos,
        portfolio = this.portfolio,
        self = this.self
    )
}

fun ParticipantDto.toSponsor(): Sponsor {
    return Sponsor(
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
    )
}

fun PhotoUserDto.toPhotoUser(): PhotoUser {
    return PhotoUser(
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
    )
}

fun BreadcrumbDto.toBreadcrumb(): Breadcrumb {
    return Breadcrumb(
        title = this.title
    )
}

fun CurrentUserCollectionDto.toCurrentUserCollection(): CurrentUserCollection {
    return CurrentUserCollection(
        id = this.id,
        title = this.title,
        description = this.description,
        publishedAt = this.publishedAt,
        lastCollectedAt = this.lastCollectedAt,
        updatedAt = this.updatedAt,
        featured = this.featured,
        totalPhotos = this.totalPhotos,
        private = this.private,
        shareKey = this.shareKey,
        links = this.links.toLinks()
    )
}

fun ExifDto.toExif(): Exif {
    return Exif(
        make = this.make,
        model = this.model,
        name = this.name,
        exposureTime = this.exposureTime,
        aperture = this.aperture,
        focalLength = this.focalLength,
        iso = this.iso
    )
}

fun LocationDto.toLocation(): Location {
    return Location(
        name = this.name,
        city = this.city,
        country = this.country,
        position = this.position.toPosition()
    )
}

fun PositionDto.toPosition(): Position {
    return Position(
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun TopicSubmissionsDto.toTopicSubmissions(): TopicSubmissions {
    return TopicSubmissions(
        texturesPatterns = this.texturesPatterns?.toCategory(),
        threeDRenders = this.threeDRenders?.toCategory(),
        architectureInterior = this.architectureInterior?.toCategory(),
        streetPhotograph = this.streetPhotograph?.toCategory(),
        fashionBeauty = this.fashionBeauty?.toCategory(),
        illustrationWallpapers = this.illustrationWallpapers?.toCategory(),
        threeD = this.threeD?.toCategory(),
        handDrawn = this.handDrawn?.toCategory(),
        lineArt = this.lineArt?.toCategory(),
        wallpapers = this.wallpapers?.toCategory(),
        nature = this.nature?.toCategory(),
        film = this.film?.toCategory(),
        people = this.people?.toCategory(),
        experimental = this.experimental?.toCategory(),
        travel = this.travel?.toCategory(),
        patterns = this.patterns?.toCategory(),
        flat = this.flat?.toCategory(),
        icons = this.icons?.toCategory()
    )
}

fun CategoryDto.toCategory(): Category {
    return Category(
        status = this.status,
        approvedOn = this.approvedOn
    )
}

fun TopicSimpleDto.toTopicSimple(): TopicSimple {
    return TopicSimple(
        id = this.id,
        slug = this.slug,
        title = this.title,
        visibility = this.visibility
    )
}

fun RelatedCollectionDto.toRelatedCollection(): RelatedCollection {
    return RelatedCollection(
        total = this.total,
        type = this.type,
        results = this.results?.map { it.toCollection() }
    )
}
