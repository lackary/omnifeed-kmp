package io.lackstudio.module.kmp.apiclient.unsplash.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchResults<T>(
    val total: Int,
    val totalPages: Int,
    val results: List<T>
)
