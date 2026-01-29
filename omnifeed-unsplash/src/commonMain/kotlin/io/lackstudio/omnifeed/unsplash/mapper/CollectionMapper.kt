package io.lackstudio.omnifeed.unsplash.mapper

import io.lackstudio.omnifeed.unsplash.data.model.response.CollectionResponse
import io.lackstudio.omnifeed.unsplash.data.model.scheme.LinksScheme
import io.lackstudio.omnifeed.unsplash.domain.model.Collection
import io.lackstudio.omnifeed.unsplash.domain.model.Links

fun CollectionResponse.toCollection(): Collection {
    return Collection(
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
        links = this.links.toLinks(),
        user = this.user.toPhotoUser(),
        coverPhoto = this.coverPhoto?.toPhotoDetail(),
        previewPhotos = this.previewPhotos?.map { it.toPhotoDetail() },
        meta = this.meta?.toMeta(),
        mediaTypes = this.mediaTypes
    )
}

fun LinksScheme.toLinks(): Links {
    return Links(
        self = this.self,
        html = this.html,
        photos = this.photos,
        related = this.related
    )
}
