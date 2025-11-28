package io.lackstudio.module.kmp.apiclient.unsplash.mapper

import io.lackstudio.module.kmp.apiclient.unsplash.data.model.response.TopicResponse
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.Topic

fun TopicResponse.toTopic(): Topic {
    return Topic(
        id = this.id,
        slug = this.slug,
        title = this.title,
        description = this.description,
        publishedAt = this.publishedAt,
        updatedAt = this.updatedAt,
        startsAt = this.startsAt,
        endsAt = this.endsAt,
        visibility = this.visibility,
        featured = this.featured,
        totalPhotos = this.totalPhotos,
        links = this.links.toLinks(),
        mediaTypes = this.mediaTypes,
        status = this.status,
        owners = this.owners.map { it.toSponsor() },
        topContributors = this.topContributors?.map { it.toSponsor() },
        coverPhoto = this.coverPhoto.toPhotoDetail(),
        previewPhotos = this.previewPhotos.map { it.toPhotoDetail() }
    )
}
