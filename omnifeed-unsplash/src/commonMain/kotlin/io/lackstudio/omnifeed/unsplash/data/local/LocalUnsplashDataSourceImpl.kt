package io.lackstudio.omnifeed.unsplash.data.local

import io.lackstudio.omnifeed.unsplash.data.remote.model.response.PhotoDetailResponse

class LocalUnsplashDataSourceImpl(/* inject your Room DAO here */) : LocalUnsplashPhotoDataSource {
    override suspend fun getPhoto(id: String): PhotoDetailResponse? {
        // TODO: implement with Room DAO
        return null
    }

    override suspend fun savePhoto(photo: PhotoDetailResponse) {
        TODO("Not yet implemented")
    }

}
