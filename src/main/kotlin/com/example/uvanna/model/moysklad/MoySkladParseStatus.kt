package com.example.uvanna.model.moysklad

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoySkladParseStatus(
    @SerialName("meta")
    val meta: MoySkladMeta,
    @SerialName("id")
    val id: String,
    @SerialName("accountId")
    val accountId: String,
    @SerialName("state")
    val state: String,
    @SerialName("request")
    val request: String,
    @SerialName("resultUrl")
    val resultUrl: String? = null
)