package com.example.uvanna.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreditResponse (
    @SerialName("id")
    val id: String = "",
    @SerialName("link")
    val link: String = ""
)