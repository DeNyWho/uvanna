package com.example.uvanna.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheckStatePaymentRequest(
    @SerialName("TerminalKey")
    val terminalKey: String = "",
    @SerialName("PaymentId")
    val paymentId: String = "",
    @SerialName("Token")
    val token: String = ""
)