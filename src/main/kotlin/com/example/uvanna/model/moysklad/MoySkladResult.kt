package com.example.uvanna.model.moysklad

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoySkladResult(
    @SerialName("rows")
    val rows: List<MoySkladProduct> = listOf()
)