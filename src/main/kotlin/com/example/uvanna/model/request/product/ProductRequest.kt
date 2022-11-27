package com.example.uvanna.model.request.product

data class ProductRequest(
    var title: String = "",
    var brand: String = "",
    var secondSub: String = "",
    var thirdSub: String = "",
    var price: Int = 0,
    var stock: Int = 0,
    var charactTitle: List<String>,
    var charactData: List<String>,
)