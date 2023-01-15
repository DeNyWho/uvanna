package com.example.uvanna.jpa

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "Brands")
data class ProductBrands(
    @Id
    var id: String = "",
    val title: String = ""
)