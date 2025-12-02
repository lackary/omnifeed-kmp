package io.lackstudio.omnifeed.unsplash.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val status: String,
    val approvedOn: String?
)
