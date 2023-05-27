package com.example.uvanna.model.moysklad

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoySkladProduct(
    @SerialName("name")
    val name: String = "",
    @SerialName("salePrices")
    val salePrices: List<MoySkladSalePrices> = listOf(),
    @SerialName("quantity")
    val quantity: Double
)