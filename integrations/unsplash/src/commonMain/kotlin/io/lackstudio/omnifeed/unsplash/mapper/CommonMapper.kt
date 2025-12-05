package io.lackstudio.omnifeed.unsplash.mapper

import io.lackstudio.omnifeed.unsplash.data.model.scheme.MetaScheme
import io.lackstudio.omnifeed.unsplash.data.model.scheme.TagScheme
import io.lackstudio.omnifeed.unsplash.domain.model.Meta
import io.lackstudio.omnifeed.unsplash.domain.model.Tag

fun MetaScheme.toMeta(): Meta {
    return Meta(
        index = this.index
    )
}

fun TagScheme.toTag(): Tag {
    return Tag(
        type = this.type,
        title = this.title
    )
}
