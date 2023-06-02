package com.example.uvanna.model.moysklad.product

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoySkladProduct(
    @SerialName("name")
    val name: String = "",
    @SerialName("id")
    val id: String = "",
    @SerialName("salePrices")
    val salePrices: List<MoySkladSalePrices> = listOf(),
    @SerialName("quantity")
    val quantity: Double
)