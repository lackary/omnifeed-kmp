package io.lackstudio.omnifeed.unsplash.data.model.response

import io.lackstudio.omnifeed.unsplash.utils.constants.ApiKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse<T>(
    val total: Int,
    @SerialName(ApiKeys.Search.TOTAL_PAGES) val totalPages: Int,
    val results: List<T>
)
