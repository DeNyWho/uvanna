package com.example.uvanna.model.payment.receipt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Receipt(
    @SerialName("Email")
    val email: String = "",
    @SerialName("Phone")
    val phone: String = "",
    @SerialName("Taxation")
    val taxation: String = "usn_income",
    @SerialName("Items")
    val items: List<Items> = listOf(),
)