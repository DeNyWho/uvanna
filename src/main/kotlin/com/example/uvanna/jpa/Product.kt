package com.example.uvanna.jpa

import javax.persistence.*

@Entity
@Table(name = "product")
data class Product(
    @Id
    val id: String? = "",
    @OneToMany
    var images: List<Image> = listOf(Image()),
    val updated: String = "",
    val name: String = "",
    val group: String = "",
    val salePrice: Int = 0,
    val buyPrice: Int = 0,
    val stock: Int = 0,
)