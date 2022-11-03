package com.example.uvanna.jpa

import javax.persistence.*

@Entity
@Table(name = "CatalogThird")
data class CatalogThird(
    @Id
    var id: String? = "",
    var title: String? = "",
)