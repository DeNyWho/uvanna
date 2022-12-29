package com.example.uvanna.model.response

import java.time.LocalDateTime


data class BlogLighterResponse(
    val id: String = "",
    val title: String = "",
    val image: String = "",
    val dateCreated: LocalDateTime? = null,
)