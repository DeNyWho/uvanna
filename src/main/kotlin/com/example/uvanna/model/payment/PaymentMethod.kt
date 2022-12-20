package com.example.uvanna.model.payment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentMethod(
    @SerialName("type")
    val type: String = "",
    @SerialName("id")
    val id: String = "",
    @SerialName("saved")
    val saved: Boolean = false,
    @SerialName("title")
    val title: String = "",
    @SerialName("account_number")
    val accountNumber: String = "",
)