package com.example.uvanna.model.orders

import com.example.uvanna.jpa.Services
import com.example.uvanna.model.OrdersProducts

data class OrderRequest(
    var id: String = "",
    val city: String = "",
    val streetFull: String = "",
    val fullName: String = "",
    val phone: String = "",
    val email: String = "",
    val typePayment: String = "",
    val typeDelivery: String = "",
    val code: String = "",
    val price: Double = 0.0,
    val paymentID: String? = null,
    val paymentSuccess: String? = null,
    val products: MutableSet<OrdersProducts> = mutableSetOf(),
    val servicesPdf: MutableSet<Services> = mutableSetOf(),
    val status: String = "",
    val updated: String = "",
    val emailSend: Boolean? = null,
    val orderFiles: MutableSet<String> = mutableSetOf()
)