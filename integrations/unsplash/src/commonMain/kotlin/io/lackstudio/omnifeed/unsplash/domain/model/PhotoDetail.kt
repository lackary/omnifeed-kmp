package io.lackstudio.omnifeed.unsplash.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PhotoDetail(
    val id: String,
    val slug: String,
    val alternativeSlugs: AlternativeSlugs,
    val createdAt: String,
    val updatedAt: String,
    val promotedAt: String?,
    val width: Int,
    val height: Int,
    val color: String,
    val blurHash: String,
    val description: String?,
    val altDescription: String?,
    val breadcrumbs: List<Breadcrumb>?,
    val urls: PhotoUrls,
    val links: PhotoLinks,
    val likes: Int,
    val likedByUser: Boolean,
    val bookmarked: Boolean,
    val currentUserCollections: List<CurrentUserCollection>?,
    val sponsorship: Sponsorship?,
    val topicSubmissions: TopicSubmissions,
    val assetType: String,
    val showOnProfile: Boolean?,
    val user: PhotoUser,
    val exif: Exif,
    val location: Location,
    val meta: Meta,
    val publicDomain: Boolean,
    val tags: List<Tag>,
    val views: Long,
    val downloads: Long,
    val topics: List<TopicSimple>,
    val relatedCollections: RelatedCollection?
)
