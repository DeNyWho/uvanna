package com.example.uvanna.model.request.payment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DataRequest(
    @SerialName("Phone")
    val phone: String = "",
    @SerialName("Email")
    val email: String = ""
)