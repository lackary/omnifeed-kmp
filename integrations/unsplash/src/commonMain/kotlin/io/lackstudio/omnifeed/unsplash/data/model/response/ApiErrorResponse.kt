package io.lackstudio.omnifeed.unsplash.data.model.response

import io.lackstudio.omnifeed.unsplash.utils.constants.ApiKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponse(
    val errors: List<String>? = emptyList(),
    val status: Int? = null,
    val error: String? = null,
    @SerialName(ApiKeys.Error.ERROR_DESCRIPTION) val errorDescription: String? = null
)
