package com.example.uvanna.model.payment.receipt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Receipt(
    @SerialName("customer")
    val customer: Customer = Customer(),
    @SerialName("items")
    val items: List<Items> = listOf(),
)