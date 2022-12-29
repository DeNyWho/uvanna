package com.example.uvanna.model.response

import com.example.uvanna.jpa.Orders
import com.example.uvanna.jpa.Product
import com.example.uvanna.model.orders.OrderConverterNeedPaid
import kotlinx.serialization.SerialName

data class OrderFullResponsePaid(
    @SerialName("Order")
    val order: Orders,
    @SerialName("Payment")
    val orderConverterNeedPaid: OrderConverterNeedPaid,
    @SerialName("Products")
    val products: List<ProductsWithCount> = listOf()
)