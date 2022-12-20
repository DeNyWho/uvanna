package com.example.uvanna.model.request.payment

import com.example.uvanna.model.payment.Settlements
import com.example.uvanna.model.payment.receipt.Customer
import com.example.uvanna.model.payment.receipt.Items
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReceiptRequest(
    @SerialName("payment_id")
    val paymentId: String = "",
    val type: String = "",
    val customer: Customer = Customer(),
    val items: List<Items> = listOf(),
    val send: Boolean? = null,
    val settlements: List<Settlements> = listOf()
)
