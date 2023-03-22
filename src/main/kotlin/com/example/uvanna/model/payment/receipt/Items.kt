package com.example.uvanna.model.payment.receipt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Items(
    @SerialName("Name")
    val name: String = "Товар UVanna.store",
    @SerialName("Price")
    val price: Int = 0,
    @SerialName("Quantity")
    val quantity: Int = 0,
    @SerialName("Amount")
    val amount: Int = 0,
    @SerialName("Tax")
    val tax: String = "none",
)