package io.lackstudio.omnifeed.unsplash.data.model.dto

import io.lackstudio.omnifeed.unsplash.data.model.scheme.BadgeScheme
import io.lackstudio.omnifeed.unsplash.data.model.scheme.MetaScheme
import io.lackstudio.omnifeed.unsplash.data.model.scheme.PreviewPhotoScheme
import io.lackstudio.omnifeed.unsplash.data.model.scheme.TagsScheme

interface UserProfileDto {
    val photos: List<PreviewPhotoScheme>
    val badge: BadgeScheme?
    val tags: TagsScheme?
    val allowMessages: Boolean?
    val numericId: Long?
    val downloads: Long?
    val meta: MetaScheme?
}
