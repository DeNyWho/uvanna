package com.example.uvanna.model.moysklad

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoySkladSalePrices(
    @SerialName("value")
    val value: Double = 0.0,
    @SerialName("priceType")
    val priceType: MoySkladPriceType = MoySkladPriceType()
)