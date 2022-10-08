package com.example.uvanna.model.product.detail

data class ProductDetail(
    val id: String = "",
    val image: String = "",
    val updated: String = "",
    val name: String = "",
    val description: String = "",
    val group: String = "",
    val minPrice: Int = 0,
    val salePrice: Int = 0,
    val buyPrice: Int = 0,
    val stock: Int = 0,
)
