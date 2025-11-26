package io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme

import io.lackstudio.module.kmp.apiclient.unsplash.utils.constants.ApiKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryScheme (
    val status: String,
    @SerialName(ApiKeys.Topic.APPROVED_ON) val approvedOn: String? = null
)
