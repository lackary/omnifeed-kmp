package io.lackstudio.omnifeed.core.common.extension

import co.touchlab.kermit.Logger

private val logger = Logger.withTag("ResultExtension")

/**
 *  Generic helper function for handling Result<T> and converting to R
 * If Result is successful, execute the transformer function to convert T to R
 * If Result fails, throw a standardized AppException
 */
suspend fun <T, R> Result<T>.toDomain(name: String = "UnknownDomain", transformer: suspend (T) -> R): R {
    return this.fold(
        onSuccess = { data ->
            logger.d { "toDomain: transforming $name" }
            // If successful, execute the transformer function
            transformer(data)
        },
        onFailure = { exception ->
            // If it fails, rethrow the standardized AppException
            logger.e(exception) { "toDomain: error transforming $name" }
            throw exception
        }
    )
}
