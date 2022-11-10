package com.example.uvanna.model.product

data class ProductsLightResponse(
    val id: String,
    val title: String,
    val imageUrls: List<String>,
    val price: Int,
    val stock: Int
)