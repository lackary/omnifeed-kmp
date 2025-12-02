package io.lackstudio.omnifeed.unsplash.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Topic(
    val id: String,
    val slug: String,
    val title: String,
    val description: String,
    val publishedAt: String,
    val updatedAt: String?,
    val startsAt: String,
    val endsAt: String?,
    val visibility: String,
    val featured: Boolean,
    val totalPhotos: Int,
    val links: Links,
    val mediaTypes: List<String>,
    val status: String,
    val owners: List<Sponsor>,
    val topContributors: List<Sponsor>?,
    val coverPhoto: Photo,
    val previewPhotos: List<PreviewPhoto>
)
