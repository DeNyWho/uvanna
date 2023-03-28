package com.example.uvanna.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheckStatePaymentResponse(
    @SerialName("TerminalKey")
    val terminalKey: String = "",
    @SerialName("OrderId")
    val orderId: String = "",
    @SerialName("Success")
    val success: Boolean = false,
    @SerialName("Status")
    val status: String = "",
    @SerialName("PaymentId")
    val paymentId: String = "",
    @SerialName("ErrorCode")
    val errorCode: String = "",
    @SerialName("Message")
    val message: String = "",
    @SerialName("Amount")
    val amount: Int = 0
)