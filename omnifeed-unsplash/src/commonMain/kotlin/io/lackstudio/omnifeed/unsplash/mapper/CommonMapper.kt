package io.lackstudio.omnifeed.unsplash.mapper

import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.MetaDto
import io.lackstudio.omnifeed.unsplash.data.remote.model.dto.TagDto
import io.lackstudio.omnifeed.unsplash.domain.model.Meta
import io.lackstudio.omnifeed.unsplash.domain.model.Tag

fun MetaDto.toMeta(): Meta {
    return Meta(
        index = this.index
    )
}

fun TagDto.toTag(): Tag {
    return Tag(
        type = this.type,
        title = this.title
    )
}
