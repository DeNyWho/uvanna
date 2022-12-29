package com.example.uvanna.model.response

import com.example.uvanna.jpa.Orders
import com.example.uvanna.model.orders.OrderConverterPaid
import kotlinx.serialization.SerialName

data class OrderFullResponseNoPaid(
    @SerialName("Order")
    val order: Orders,
    @SerialName("Payment")
    val orderConverterPaid: OrderConverterPaid,
    @SerialName("Products")
    val products: List<ProductsWithCount> = listOf()
)