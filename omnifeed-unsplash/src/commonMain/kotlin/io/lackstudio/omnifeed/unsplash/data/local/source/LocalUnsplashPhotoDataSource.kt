package io.lackstudio.omnifeed.unsplash.data.local.source

import io.lackstudio.omnifeed.unsplash.data.remote.model.response.PhotoDetailResponse

interface LocalUnsplashPhotoDataSource {
    suspend fun getPhoto(id: String): PhotoDetailResponse?
    suspend fun savePhoto(photo: PhotoDetailResponse)
}
