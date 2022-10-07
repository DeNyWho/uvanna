package com.example.uvanna.model.product

data class Product(
//    val externalCode: String,
    val image: String,
    val updated: String,
    val name: String,
    val group: String,
    val minPrice: Int,
    val salePrice: Int,
    val buyPrice: Int,
    val stock: Int,
)