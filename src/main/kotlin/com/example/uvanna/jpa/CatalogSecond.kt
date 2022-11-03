package com.example.uvanna.jpa

import javax.persistence.*

@Entity
@Table(name = "CatalogSecond")
data class CatalogSecond(
    @Id
    var id: String? = "",
    var title: String? = "",
    @OneToMany
    @Column(nullable = true)
    var sub: List<CatalogThird> = listOf(),
    val imageUrl: String? = null,
)