package io.lackstudio.module.kmp.apiclient.unsplash.mapper

import io.lackstudio.module.kmp.apiclient.unsplash.data.model.response.CollectionResponse
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.LinksScheme
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.Collection
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.Links

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
