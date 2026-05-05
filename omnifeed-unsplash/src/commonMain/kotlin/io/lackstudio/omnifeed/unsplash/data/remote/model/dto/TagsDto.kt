package io.lackstudio.omnifeed.unsplash.data.remote.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class TagsDto(
    val custom: List<TagDto>,
    val aggregated: List<TagDto>
)
