package com.example.uvanna.model.response

import com.example.uvanna.jpa.Product
import kotlinx.serialization.Serializable

@Serializable
data class ProductsWithCount(
    val product: Product,
    val count: Int
)
