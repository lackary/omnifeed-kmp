package io.lackstudio.omnifeed.unsplash.data.model.response

import io.lackstudio.omnifeed.core.network.oauth.model.BaseTokenResponse
import io.lackstudio.omnifeed.unsplash.utils.constants.ApiKeys
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    @SerialName(ApiKeys.Token.ACCESS_TOKEN) override val accessToken: String,
    @SerialName(ApiKeys.Token.TOKEN_TYPE) override val tokenType: String,
    @SerialName(ApiKeys.Token.REFRESH_TOKEN) override val refreshToken: String,
    override val scope: String,
    @SerialName(ApiKeys.Common.CREATED_AT) val createdAt: Long,
    @SerialName(ApiKeys.Token.USER_ID) val userId: Long,
    val username: String
) : BaseTokenResponse
