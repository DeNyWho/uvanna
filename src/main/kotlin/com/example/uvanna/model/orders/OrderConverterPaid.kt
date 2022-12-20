package com.example.uvanna.model.orders

import com.example.uvanna.model.payment.Amount
import com.example.uvanna.model.payment.Recipient
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable()
data class OrderConverterPaid(
    @SerialName("id")
    val id: String,
    @SerialName("status")
    val status: String,
    @SerialName("amount")
    val amount: Amount,
    @SerialName("recipient")
    val recipient: Recipient,
    @SerialName("created_at")
    val created_at: String,
    val test: Boolean,
    @SerialName("paid")
    val paid: Boolean,
)