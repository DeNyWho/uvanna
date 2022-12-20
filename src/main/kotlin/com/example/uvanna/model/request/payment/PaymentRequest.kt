package com.example.uvanna.model.request.payment

import com.example.uvanna.model.payment.Amount
import com.example.uvanna.model.payment.Confirmation
import com.example.uvanna.model.payment.receipt.Receipt
import kotlinx.serialization.Serializable

@Serializable
data class PaymentRequest(
    val amount: Amount = Amount(),
    val confirmation: Confirmation = Confirmation(),
    val capture: Boolean = true,
    val test: Boolean = true,
    val receipt: Receipt = Receipt()
)