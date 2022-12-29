package com.example.uvanna.model.request.product

data class ProductRequest(
    var title: String = "",
    var brand: String = "",
    var firstSub: String = "",
    var secondSub: String = "",
    var thirdSub: String = "",
    var price: Int = 0,
    var stock: Int = 0,
    val sellPrice: Int? = null,
    var charactTitle: List<String> = listOf(),
    var charactData: List<String> = listOf(),
)