package com.example.uvanna.jpa

import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "Promo")
data class Promo(
    @Id
    var id: String = "",
    var title: String? = "",
    val description: String? = "",
    val imageUrl: String? = null,
    val date: LocalDate? = null,
)