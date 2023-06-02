package com.example.uvanna.model.moysklad.product

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoySkladPriceType(
    @SerialName("name")
    val name: String = "Цена продажи"
)