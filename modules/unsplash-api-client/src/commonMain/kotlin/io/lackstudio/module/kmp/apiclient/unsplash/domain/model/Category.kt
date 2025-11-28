package io.lackstudio.module.kmp.apiclient.unsplash.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val status: String,
    val approvedOn: String?
)
