package io.lackstudio.omnifeed.auth.data.remote.model.response

import io.lackstudio.omnifeed.auth.data.remote.model.dto.UserInfoDto
import kotlinx.serialization.Serializable

@Serializable
data class LookupResponse(
    val users: List<UserInfoDto> = emptyList()
)
