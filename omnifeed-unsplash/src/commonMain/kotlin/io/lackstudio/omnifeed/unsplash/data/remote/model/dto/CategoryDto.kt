package io.lackstudio.omnifeed.unsplash.data.remote.model.dto

import io.lackstudio.omnifeed.unsplash.utils.constants.ApiKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto (
    val status: String,
    @SerialName(ApiKeys.Topic.APPROVED_ON) val approvedOn: String? = null
)
