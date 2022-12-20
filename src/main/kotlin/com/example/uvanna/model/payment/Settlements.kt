package com.example.uvanna.model.payment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Settlements(
    @SerialName("type")
    val type: String = "",
    @SerialName("amount")
    val amount: Amount= Amount()
)
