package com.example.uvanna.model.payment

import kotlinx.serialization.Serializable

@Serializable
data class Amount(
    val value: String = "",
    val currency: String = "",
)
