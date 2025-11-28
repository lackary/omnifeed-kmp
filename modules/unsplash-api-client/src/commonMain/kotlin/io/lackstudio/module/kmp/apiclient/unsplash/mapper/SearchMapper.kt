package io.lackstudio.module.kmp.apiclient.unsplash.mapper

import io.lackstudio.module.kmp.apiclient.unsplash.data.model.response.SearchResponse
import io.lackstudio.module.kmp.apiclient.unsplash.domain.model.SearchResults

fun <T, R> SearchResponse<T>.toSearchResults(mapper: (T) -> R): SearchResults<R> {
    return SearchResults(
        total = this.total,
        totalPages = this.totalPages,
        results = this.results.map(mapper)
    )
}
