package io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme

import kotlinx.serialization.Serializable

@Serializable
data class TopicSimpleScheme(
    val id: String,
    val slug: String,
    val title: String,
    val visibility: String,
)
