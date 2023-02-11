package com.example.uvanna.model.orders

data class ServiceRequest(
    var serviceName: String = "",
    val count: Int = 0,
    val price: Int = 0
)