package com.example.uvanna.model.request.payment

import kotlinx.serialization.Serializable

@Serializable
data class ProductsRequestsing(
    val product: String = "",
    val count: Int = 0,
)