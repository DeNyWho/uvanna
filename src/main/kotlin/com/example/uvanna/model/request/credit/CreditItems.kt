package com.example.uvanna.model.request.credit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CreditItems(
    @SerialName("name")
    val name: String = "Товар UVanna.store",
    @SerialName("quantity")
    val quantity: Int = 0,
    @SerialName("price")
    val price: Int = 0
)