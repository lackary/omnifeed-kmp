package io.lackstudio.omnifeed.unsplash.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Tags(
    val custom: List<Tag>,
    val aggregated: List<Tag>
)
