package io.lackstudio.omnifeed.unsplash.data.local

import io.lackstudio.omnifeed.unsplash.data.model.response.PhotoDetailResponse

interface LocalUnsplashPhotoDataSource {
    suspend fun getPhoto(id: String): PhotoDetailResponse?
    suspend fun savePhoto(photo: PhotoDetailResponse)
}
