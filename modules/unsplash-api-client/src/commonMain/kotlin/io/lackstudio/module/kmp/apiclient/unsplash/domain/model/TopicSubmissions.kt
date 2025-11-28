package io.lackstudio.module.kmp.apiclient.unsplash.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TopicSubmissions(
    val texturesPatterns: Category?,
    val threeDRenders: Category?,
    val architectureInterior: Category?,
    val streetPhotograph: Category?,
    val fashionBeauty: Category?,
    val illustrationWallpapers: Category?,
    val threeD: Category?,
    val handDrawn: Category?,
    val lineArt: Category?,
    val wallpapers: Category?,
    val nature: Category?,
    val film: Category?,
    val people: Category?,
    val experimental: Category?,
    val travel: Category?,
    val patterns: Category?,
    val flat: Category?,
    val icons: Category?
)
