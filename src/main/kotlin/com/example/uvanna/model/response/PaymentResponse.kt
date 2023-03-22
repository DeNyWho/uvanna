package com.example.uvanna.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentResponse(
    @SerialName("Success")
    val success: Boolean = false,
    @SerialName("ErrorCode")
    val errorCode: String = "0",
    @SerialName("TerminalKey")
    val terminalKey: String = "TinkoffBankTest",
    @SerialName("Status")
    val status: String = "NEW",
    @SerialName("PaymentId")
    val paymentId: String = "0",
    @SerialName("OrderId")
    val orderId: String = "0",
    @SerialName("Amount")
    val amount: Int = 0,
    @SerialName("PaymentURL")
    val paymentURL: String = "",
)