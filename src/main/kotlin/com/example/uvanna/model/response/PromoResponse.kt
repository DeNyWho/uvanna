package com.example.uvanna.model.response

import com.example.uvanna.jpa.Product
import java.time.LocalDate
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Id

data class PromoResponse(
    val id: String = "",
    val title: String? = "",
    val description: String? = "",
    val imageUrl: String? = null,
    val dateCreated: LocalDate? = null,
    val dateExpired: LocalDate? = null,
    val productPromo: List<Product> = mutableListOf()
)