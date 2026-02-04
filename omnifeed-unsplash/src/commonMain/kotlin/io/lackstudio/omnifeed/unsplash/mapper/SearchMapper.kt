package io.lackstudio.omnifeed.unsplash.mapper

import io.lackstudio.omnifeed.unsplash.data.model.response.SearchResponse
import io.lackstudio.omnifeed.unsplash.domain.model.SearchResults

fun <T, R> SearchResponse<T>.toSearchResults(mapper: (T) -> R): SearchResults<R> {
    return SearchResults(
        total = this.total,
        totalPages = this.totalPages,
        results = this.results.map(mapper)
    )
}
