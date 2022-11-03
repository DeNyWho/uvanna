package com.example.uvanna.jpa

import javax.persistence.*

@Entity
@Table(name = "CatalogFirst")
data class CatalogFirst(
    @Id
    var id: String? = "",
    var title: String? = "",
    @OneToMany
    @Column(nullable = true)
    var sub: List<CatalogSecond> = listOf(),
    val imageUrl: String = "",
)