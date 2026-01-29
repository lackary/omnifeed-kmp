package io.lackstudio.omnifeed.unsplash.data.model.scheme

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
    @SerialName("textures-patterns") val texturesPatterns: CategoryScheme? = null,
    @SerialName("3d-renders") val threeDRenders: CategoryScheme? = null,
    @SerialName("architecture-interior") val architectureInterior: CategoryScheme? = null,
    @SerialName("street-photograph") val streetPhotograph: CategoryScheme? = null,
    @SerialName("fashion-beauty") val fashionBeauty: CategoryScheme? = null,
    @SerialName("illustration-wallpapers") val illustrationWallpapers: CategoryScheme? = null,
    @SerialName("3d") val threeD: CategoryScheme? = null,
    @SerialName("hand-drawn") val handDrawn: CategoryScheme? = null,
    @SerialName("line-art") val lineArt: CategoryScheme? = null,
    val wallpapers: CategoryScheme? = null,
    val nature: CategoryScheme? = null,
    val film: CategoryScheme? = null,
    val people: CategoryScheme? = null,
    val experimental: CategoryScheme? = null,
    val travel: CategoryScheme? = null,
    val patterns: CategoryScheme? = null,
    val flat: CategoryScheme? = null,
    val icons: CategoryScheme? = null
)
