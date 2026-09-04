package io.lackstudio.omnifeed.core.utils

import co.touchlab.kermit.Logger
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64

@PublishedApi
internal val logger = Logger.withTag("StringHelper")

fun String?.maskId(): String {
    if (this == null) return "null"
    if (length <= 20) return this
    return "${take(10)}...${takeLast(10)}"
}

inline fun <reified T> base64ToJson(base64String: String): T? {
    val json = Json { ignoreUnknownKeys = true }
    return try {
        val decodedBytes = Base64.decode(base64String)
        val jsonString = decodedBytes.decodeToString()
        json.decodeFromString<T>(jsonString)
    } catch (e: Exception) {
        logger.e(throwable = e) {"base64 decode exception or json decode exception"}
        null
    }
}
