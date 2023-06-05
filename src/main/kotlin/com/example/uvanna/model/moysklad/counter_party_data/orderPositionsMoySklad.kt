package com.example.uvanna.model.moysklad.counter_party_data

import kotlinx.serialization.Serializable

@Serializable
data class orderPositionsMoySklad(
    val quantity: Int,
    val price: Double,
    val discount: Double,
    val assortment: AssortmentSklad
)