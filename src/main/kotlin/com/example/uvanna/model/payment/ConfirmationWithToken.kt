package com.example.uvanna.model.payment

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmationWithToken(
    val type: String = "embedded",
    val confirmation_token: String = "",
)