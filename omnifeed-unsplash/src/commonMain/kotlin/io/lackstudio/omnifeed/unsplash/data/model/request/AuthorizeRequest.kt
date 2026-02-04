package io.lackstudio.omnifeed.unsplash.data.model.request

import io.ktor.resources.Resource
import io.lackstudio.omnifeed.core.network.oauth.model.BaseAuthorizeRequest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Resource("/oauth/authorize")
data class AuthorizeRequest(
    @SerialName("client_id") override val clientId: String,
    @SerialName("redirect_uri") override val redirectUri: String,
    @SerialName("response_type") override val responseType: String,
    override val scope: String,
    override val state: String? = null
) : BaseAuthorizeRequest
