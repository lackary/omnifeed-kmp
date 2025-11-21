package io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme

import io.lackstudio.module.kmp.apiclient.unsplash.data.model.scheme.TexturesPatternsScheme
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/*
"topic_submissions": {
    "architecture-interior": {
        "status": "unevaluated"
    },
    "experimental": {
        "status": "rejected"
    },
    "film": {
        "status": "approved",
        "approved_on": "2025-07-24T13:34:36Z"
    },
    "wallpapers": {
        "status": "rejected"
    }
}
*/


//@Serializable
//data class TopicSubmissions(
//    val
//)
@Serializable
data class TopicSubmissionsScheme(
    @SerialName("textures-patterns") val texturesPatterns: TexturesPatternsScheme
)
