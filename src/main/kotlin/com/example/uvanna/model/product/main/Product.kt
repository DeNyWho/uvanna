package com.example.uvanna.model.product.main

data class Product(
    val id: String = "",
    val image: List<String> = listOf(""),
    val updated: String = "",
    val name: String = "",
    val group: String = "",
    val minPrice: Int = 0,
    val salePrice: Int = 0,
    val buyPrice: Int = 0,
    val stock: Int = 0,
)