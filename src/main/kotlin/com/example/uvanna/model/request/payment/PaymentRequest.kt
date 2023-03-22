package com.example.uvanna.model.request.payment

import com.example.uvanna.model.payment.receipt.Receipt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentRequest(
    @SerialName("TerminalKey")
    val terminalKey: String = "TinkoffBankTest",
    @SerialName("Amount")
    val amount: Int = 0,
    @SerialName("OrderId")
    val orderID: String = "",
    @SerialName("Description")
    val description: String = "",
    @SerialName("DATA")
    val data: DataRequest = DataRequest(),
    @SerialName("Receipt")
    val receipt: Receipt = Receipt(),
    @SerialName("SuccessURL")
    val successURL: String = ""
)