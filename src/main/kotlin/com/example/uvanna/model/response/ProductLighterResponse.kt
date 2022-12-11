package com.example.uvanna.model.response

data class ProductLighterResponse(
    val id: String,
    val title: String,
    val imageUrls: List<String>,
    val price: Int,
    val sellPrice: Int?,
)