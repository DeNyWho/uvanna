package com.example.uvanna.model.payment

import kotlinx.serialization.Serializable

@Serializable
data class Confirmation(
    val type: String = "embedded",
    val return_url: String = "",
)