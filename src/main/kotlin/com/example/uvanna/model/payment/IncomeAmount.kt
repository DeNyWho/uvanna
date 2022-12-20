package com.example.uvanna.model.payment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IncomeAmount(
    @SerialName("value")
    val value: String = "",
    @SerialName("currency")
    val currency: String = "RUB",
)