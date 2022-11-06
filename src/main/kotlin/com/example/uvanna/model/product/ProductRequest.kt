package com.example.uvanna.model.product

data class ProductRequest(
    val title: String = "",
    val characteristic: List<Characteristic> = listOf(),
    val firstSub: String = "",
    val secondSub: String = "",
    val thirdSub: String = "",
    val price: Int = 0,
    val stock: Int = 0,
)
