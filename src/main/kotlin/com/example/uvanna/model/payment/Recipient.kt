package com.example.uvanna.model.payment

import kotlinx.serialization.Serializable

@Serializable
data class Recipient(
    val account_id: String = "",
    val gateway_id: String = "",
)