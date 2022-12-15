package com.example.uvanna.model.response

import com.example.uvanna.jpa.Orders
import com.example.uvanna.model.orders.OrderConverter
import kotlinx.serialization.SerialName

data class OrderFullResponse(
    @SerialName("Order")
    val order: Orders,
    @SerialName("Payment")
    val orderConverter: OrderConverter,

)