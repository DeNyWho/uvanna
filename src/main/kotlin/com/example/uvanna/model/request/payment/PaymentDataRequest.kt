package com.example.uvanna.model.request.payment

import kotlinx.serialization.Serializable

@Serializable
data class PaymentDataRequest(
    val city: String = "",
    val streetFull: String = "",
    val fullname: String = "",
    val phone: String = "",
    val email :String = "",
    val typePayment: String = "",
    val typeDelivery: String = "",
)