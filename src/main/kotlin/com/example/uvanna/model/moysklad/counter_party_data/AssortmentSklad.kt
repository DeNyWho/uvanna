package com.example.uvanna.model.moysklad.counter_party_data

import kotlinx.serialization.Serializable

@Serializable
data class AssortmentSklad (
    val meta: metaProductSklad,
    val reserve: Int

)