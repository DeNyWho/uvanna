package com.example.uvanna.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreditInfoResponse(
    @SerialName("id")
    val id: String = "",
    @SerialName("status")
    val status: String = "",
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("demo")
    val demo: Boolean = false,
    @SerialName("committed")
    val committed: Boolean = false,
    @SerialName("order_amount")
    val orderAmount: Double = 0.0,
    @SerialName("product")
    val product: String = "",
    @SerialName("appropriate_signing_types")
    val appropriateSigningTypes: List<String> = listOf(),
)