package com.example.uvanna.model.payment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("confirmation")
data class ConfirmationRedirect(
    @SerialName("type")
    val type: String = "",
    @SerialName("confirmation_url")
    val confirmation_url: String = "",
)