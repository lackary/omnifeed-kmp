package io.lackstudio.omnifeed.unsplash.data.remote.model.dto

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
data class TopicSubmissionsDto(
    @SerialName("textures-patterns") val texturesPatterns: CategoryDto? = null,
    @SerialName("3d-renders") val threeDRenders: CategoryDto? = null,
    @SerialName("architecture-interior") val architectureInterior: CategoryDto? = null,
    @SerialName("street-photograph") val streetPhotograph: CategoryDto? = null,
    @SerialName("fashion-beauty") val fashionBeauty: CategoryDto? = null,
    @SerialName("illustration-wallpapers") val illustrationWallpapers: CategoryDto? = null,
    @SerialName("3d") val threeD: CategoryDto? = null,
    @SerialName("hand-drawn") val handDrawn: CategoryDto? = null,
    @SerialName("line-art") val lineArt: CategoryDto? = null,
    val wallpapers: CategoryDto? = null,
    val nature: CategoryDto? = null,
    val film: CategoryDto? = null,
    val people: CategoryDto? = null,
    val experimental: CategoryDto? = null,
    val travel: CategoryDto? = null,
    val patterns: CategoryDto? = null,
    val flat: CategoryDto? = null,
    val icons: CategoryDto? = null
)
