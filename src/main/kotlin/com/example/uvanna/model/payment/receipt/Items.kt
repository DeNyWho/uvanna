package com.example.uvanna.model.payment.receipt

import com.example.uvanna.model.payment.Amount
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Items(
    @SerialName("description")
    val description: String = "Покупка на сайте U Vanna",
    @SerialName("amount")
    val amount: Amount = Amount(
        value = "",
        currency = "RUB"
    ),
    @SerialName("vat_code")
    val vatCode: Int? = null,
    @SerialName("quantity")
    val quantity: String = ""
)