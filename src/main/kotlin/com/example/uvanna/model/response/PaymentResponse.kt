package com.example.uvanna.model.response

import com.example.uvanna.model.payment.Amount
import com.example.uvanna.model.payment.ConfirmationWithToken
import com.example.uvanna.model.payment.Recipient
import kotlinx.serialization.Serializable

@Serializable
data class PaymentResponse(
    val id: String,
    val status: String,
    val amount: Amount,
    val recipient: Recipient,
    val created_at: String,
    val confirmation: ConfirmationWithToken,
    val test: String,
    val paid: String,
    val refundable: String,
    var metadata: Recipient? = null,
)