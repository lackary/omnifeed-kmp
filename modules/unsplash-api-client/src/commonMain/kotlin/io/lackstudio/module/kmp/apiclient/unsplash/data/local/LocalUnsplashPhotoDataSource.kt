package io.lackstudio.module.kmp.apiclient.unsplash.data.local

import io.lackstudio.module.kmp.apiclient.unsplash.data.model.response.PhotoDetailResponse

interface LocalUnsplashPhotoDataSource {
    suspend fun getPhoto(id: String): PhotoDetailResponse?
    suspend fun savePhoto(photo: PhotoDetailResponse)
}
