package io.lackstudio.module.kmp.apiclient.unsplash.data.model.response

import io.lackstudio.module.kmp.apiclient.unsplash.utils.constants.ApiKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse<T>(
    val total: Int,
    @SerialName(ApiKeys.Search.TOTAL_PAGES) val totalPages: Int,
    val results: List<T>
)
