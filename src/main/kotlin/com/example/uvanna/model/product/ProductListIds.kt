package com.example.uvanna.model.product

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("products")
data class ProductListIds(
    @SerialName("ids")
    val productIds: List<String> = listOf()
)