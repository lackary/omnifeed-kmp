package io.lackstudio.module.kmp.apiclient.unsplash.network

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import io.lackstudio.module.kmp.apiclient.unsplash.test.MockData
import io.lackstudio.module.kmp.apiclient.unsplash.utils.Environment
import io.lackstudio.module.kmp.apiclient.unsplash.utils.constants.ApiKeys

// 定義與測試檔案中一致的常數，以免打錯字導致測試失敗
const val MOCK_USERNAME = "pawel_czerwinski"
const val MOCK_PHOTO_ID = "4ICax0QMs8U"
const val MOCK_COLLECTION_ID = "8961198"
const val MOCK_QUERY = "Taipei"
const val MOCK_TOPIC_ID_OR_SLUG = "wallpapers"

val UnsplashMockEngine = MockEngine { request ->
    val url = request.url
    val path = url.encodedPath
    val args = url.parameters

    // 設定 JSON Header
    val responseHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    println("path: $path")
    when {
        // API /users/:username
        path == "${Environment.API_USERS}/$MOCK_USERNAME"-> {
            respond(MockData.USER_PROFILE, HttpStatusCode.OK, responseHeaders)
        }
        // API /users/:username/photos
        path == "${Environment.API_USERS}/$MOCK_USERNAME${Environment.API_PHOTOS}" -> {
            respond(MockData.USER_PHOTO_LIST, HttpStatusCode.OK, responseHeaders)
        }
        // API /user/:username/likes
        path == "${Environment.API_USERS}/$MOCK_USERNAME${Environment.API_LIKES}" -> {
            println("path into: $path")
            respond(MockData.USER_LIKED_PHOTO_LIST, HttpStatusCode.OK, responseHeaders)
        }
        // API /user/:username/collections
        path == "${Environment.API_USERS}/$MOCK_USERNAME${Environment.API_COLLECTIONS}" -> {
            respond(MockData.USER_COLLECTION_LIST, HttpStatusCode.OK, responseHeaders)
        }
        // API /photos
        path == Environment.API_PHOTOS -> {
            respond(MockData.PHOTO_LIST, HttpStatusCode.OK, responseHeaders)
        }
        // API /photos/:id
        path == "${Environment.API_PHOTOS}/$MOCK_PHOTO_ID" -> {
            respond(MockData.PHOTO, HttpStatusCode.OK, responseHeaders)
        }
        // API /search/photos
        path == "${Environment.API_SEARCH}${Environment.API_PHOTOS}" && args[ApiKeys.Params.QUERY] == MOCK_QUERY -> {
            respond(MockData.SEARCH_PHOTO_LIST, HttpStatusCode.OK, responseHeaders)
        }
        // API /search/collections
        path == "${Environment.API_SEARCH}${Environment.API_COLLECTIONS}" && args[ApiKeys.Params.QUERY] == MOCK_QUERY -> {
            respond(MockData.SEARCH_COLLECTION_LIST, HttpStatusCode.OK, responseHeaders)
        }
        // API /search/users
        path == "${Environment.API_SEARCH}${Environment.API_USERS}" && args[ApiKeys.Params.QUERY] == MOCK_QUERY -> {
            respond(MockData.SEARCH_USER_LIST, HttpStatusCode.OK, responseHeaders)
        }
        // API /collections
        path == Environment.API_COLLECTIONS -> {
            respond(MockData.COLLECTION_LIST, HttpStatusCode.OK, responseHeaders)
        }
        // API /collections/:id
        path == "${Environment.API_COLLECTIONS}/$MOCK_COLLECTION_ID" -> {
            respond(MockData.COLLECTION, HttpStatusCode.OK, responseHeaders)
        }
        // API /collections/:id/photos
        path == "${Environment.API_COLLECTIONS}/$MOCK_COLLECTION_ID${Environment.API_PHOTOS}" -> {
            respond(MockData.COLLECTION_PHOTO_LIST, HttpStatusCode.OK, responseHeaders)
        }
        // API /collections/:id/related
        path == "${Environment.API_COLLECTIONS}/$MOCK_COLLECTION_ID${Environment.API_RELATED}" -> {
            respond(MockData.COLLECTION_RELATED_LIST, HttpStatusCode.OK, responseHeaders)
        }
        // API /topics
        path == Environment.API_TOPICS -> {
            respond(MockData.TOPIC_LIST, HttpStatusCode.OK, responseHeaders)
        }
        // API /topics/:id_or_slug
        path == "${Environment.API_TOPICS}/$MOCK_TOPIC_ID_OR_SLUG" -> {
            respond(MockData.TOPIC_ID_OR_SLUG, HttpStatusCode.OK, responseHeaders)
        }
        // API /topic/:id_or_slug/photos
        path == "${Environment.API_TOPICS}/$MOCK_TOPIC_ID_OR_SLUG${Environment.API_PHOTOS}" -> {
            respond(MockData.TOPIC_ID_OR_SLUG_PHOTOS_LIST, HttpStatusCode.OK, responseHeaders)
        }
        else -> {
            // 捕捉未處理的路由，方便 Debug
            error("Unhandled Mock Request: ${request.url.encodedPath}")
        }
    }
}
