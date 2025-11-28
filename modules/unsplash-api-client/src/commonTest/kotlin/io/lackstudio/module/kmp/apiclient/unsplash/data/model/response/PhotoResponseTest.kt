package io.lackstudio.module.kmp.apiclient.unsplash.data.model.response

import io.lackstudio.module.kmp.apiclient.unsplash.test.MockData
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertNotNull

class PhotoResponseTest {
    private val json = Json {
        ignoreUnknownKeys = true // 測試時也建議開啟，模擬實際情況
        isLenient = true
    }

    @Test
    fun `UnsplashPhotoResponse should deserialize correctly when all fields are present`() {
        val photo = json.decodeFromString<PhotoDetailResponse>(MockData.PHOTO)

        assertNotNull(photo)
    }
}
