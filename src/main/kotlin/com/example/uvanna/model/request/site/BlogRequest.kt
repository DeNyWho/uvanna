package com.example.uvanna.model.request.site

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BlogRequest(
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
)