package com.example.uvanna.jpa

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "CatalogThird")
data class CatalogThird(
    @Id
    var id: String? = "",
    var title: String? = "",
    val imageUrl: String? = null,
    val level: String = "third"
)