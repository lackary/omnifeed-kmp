package io.lackstudio.module.kmp.apiclient.unsplash.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CurrentUserCollection(
    val id: String,
    val title: String,
    val description: String?,
    val publishedAt: String,
    val lastCollectedAt: String,
    val updatedAt: String,
    val featured: Boolean,
    val totalPhotos: Int,
    val private: Boolean,
    val shareKey: String,
    val links: PhotoLinks
)
