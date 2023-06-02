package com.example.uvanna.model.moysklad.product

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.Serial

@Serializable
data class MoySkladMeta(
    @SerialName("href")
    val href: String,
    @SerialName("type")
    val type: String,
    @SerialName("mediaType")
    val mediaType: String,
)