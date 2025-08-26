package br.com.b256.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PongResponse(
    @SerialName("result")
    val result: String,

    @SerialName("success")
    val success: String,
)
