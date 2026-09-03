package io.lackstudio.omnifeed.unsplash.data.remote.model.response

import io.lackstudio.omnifeed.unsplash.data.remote.model.response.PhotoDetailResponse
import io.lackstudio.omnifeed.unsplash.test.MockData
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertNotNull

class PhotoResponseTest {
    private val json = Json {
        ignoreUnknownKeys = true // Recommended to enable during testing to simulate real scenarios
        isLenient = true
    }

    @Test
    fun `UnsplashPhotoResponse should deserialize correctly when all fields are present`() {
        val photo = json.decodeFromString<PhotoDetailResponse>(MockData.PHOTO)

        assertNotNull(photo)
    }
}
