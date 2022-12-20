package com.example.uvanna.model.payment.receipt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    @SerialName("email")
    val email: String = "",
    @SerialName("phone")
    val phone: String = "",
)
