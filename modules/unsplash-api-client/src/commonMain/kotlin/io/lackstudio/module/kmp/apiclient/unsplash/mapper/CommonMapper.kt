package io.lackstudio.module.kmp.apiclient.unsplash.mapper

import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.MetaScheme
import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.TagScheme
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.Meta
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.Tag

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
