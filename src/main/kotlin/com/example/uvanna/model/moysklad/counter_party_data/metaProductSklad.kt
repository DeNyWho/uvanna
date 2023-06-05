package com.example.uvanna.model.moysklad.counter_party_data

import kotlinx.serialization.Serializable

@Serializable
data class metaProductSklad(
    val href: String,
    val type: String,
    val mediaType: String
)