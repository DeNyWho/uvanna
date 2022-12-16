package com.example.uvanna.model.response

import com.example.uvanna.jpa.Orders
import kotlinx.serialization.SerialName

data class OrderSmallResponse(
    @SerialName("Order")
    val order: Orders
)