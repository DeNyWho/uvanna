package com.example.uvanna.model.response

import kotlinx.serialization.Serializable
import org.springframework.http.HttpStatus

@Serializable
data class PagingResponse<T>(
    var data: List<T>? = null,
    var status: HttpStatus,
    var totalPages: Int = 0,
    var totalElements: Long = 0,
    var message: String = ""
)