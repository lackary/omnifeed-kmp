package io.lackstudio.module.kmp.apiclient.unsplash.mapper

import io.lackstudio.module.kmp.apiclient.unsplash.data.model.response.PhotoDetailResponse
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.AlternativeSlugsScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.BreadcrumbScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.CategoryScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.CurrentUserCollectionScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.ExifScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.LocationScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.ParticipantScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.PhotoLinksScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.PhotoScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.PhotoUrlsScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.PositionScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.RelatedCollectionScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.SponsorshipScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.TopicSimpleScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.TopicSubmissionsScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.UserLinksScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.UserScheme
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.AlternativeSlugs
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.Breadcrumb
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.Category
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.CurrentUserCollection
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.Exif
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.Location
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.Photo
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.PhotoDetail
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.PhotoLinks
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.PhotoUrls
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.PhotoUser
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.Position
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.RelatedCollection
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.Sponsor
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.Sponsorship
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.TopicSimple
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.TopicSubmissions
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.UserLinks

fun PhotoScheme.toPhotoDetail(): Photo {
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

fun AlternativeSlugsScheme.toAlternativeSlugs(): AlternativeSlugs {
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

fun PhotoLinksScheme.toPhotoLinks(): PhotoLinks {
    return PhotoLinks(
        self = this.self,
        html = this.html,
        download = this.download,
        downloadLocation = this.downloadLocation
    )
}

fun SponsorshipScheme.toSponsorship(): Sponsorship {
    return Sponsorship(
        impressionUrls = this.impressionUrls,
        sponsor = this.sponsor.toSponsor(),
        tagline = this.tagline,
        taglineUrl = this.taglineUrl
    )
}

fun PhotoUrlsScheme.toPhotoUrls(): PhotoUrls {
    return PhotoUrls(
        raw = this.raw,
        full = this.full,
        regular = this.regular,
        small = this.small,
        thumb = this.thumb,
        smallS3 = smallS3
    )
}

fun UserLinksScheme.toUserLinks(): UserLinks {
    return UserLinks(
        html = this.html,
        likes = this.photos,
        photos = this.photos,
        portfolio = this.portfolio,
        self = this.self
    )
}

fun ParticipantScheme.toSponsor(): Sponsor {
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

fun UserScheme.toPhotoUser(): PhotoUser {
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

fun BreadcrumbScheme.toBreadcrumb(): Breadcrumb {
    return Breadcrumb(
        title = this.title
    )
}

fun CurrentUserCollectionScheme.toCurrentUserCollection(): CurrentUserCollection {
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
        links = this.links.toPhotoLinks()
    )
}

fun ExifScheme.toExif(): Exif {
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

fun LocationScheme.toLocation(): Location {
    return Location(
        name = this.name,
        city = this.city,
        country = this.country,
        position = this.position.toPosition()
    )
}

fun PositionScheme.toPosition(): Position {
    return Position(
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun TopicSubmissionsScheme.toTopicSubmissions(): TopicSubmissions {
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

fun CategoryScheme.toCategory(): Category {
    return Category(
        status = this.status,
        approvedOn = this.approvedOn
    )
}

fun TopicSimpleScheme.toTopicSimple(): TopicSimple {
    return TopicSimple(
        id = this.id,
        slug = this.slug,
        title = this.title,
        visibility = this.visibility
    )
}

fun RelatedCollectionScheme.toRelatedCollection(): RelatedCollection {
    return RelatedCollection(
        total = this.total,
        type = this.type,
        results = this.results?.map { it.toCollection() }
    )
}
