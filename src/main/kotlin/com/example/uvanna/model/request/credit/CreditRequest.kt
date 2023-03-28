package com.example.uvanna.model.request.credit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CreditRequest(
    @SerialName("shopId")
    val shopId: String = UUID.randomUUID().toString(),
    @SerialName("showcaseId")
    val showcaseId: String = UUID.randomUUID().toString(),
    @SerialName("sum")
    val sum: Int = 0,
    @SerialName("items")
    val items: List<CreditItems> = listOf(),
    @SerialName("orderNumber")
    val orderID: String = "",
    @SerialName("SuccessURL")
    val successURL: String = ""
)